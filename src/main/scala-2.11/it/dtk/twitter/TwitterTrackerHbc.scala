package it.dtk.twitter

import java.util.concurrent.LinkedBlockingQueue

import akka.actor._
import MessageProtocol._
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants._
import com.twitter.hbc.core.{ Constants, HttpHosts }
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.OAuth1
import it.dtk.twitter.MessageProtocol.Status
import scala.concurrent.duration._
import scala.language.postfixOps

object TwitterTrackerHbc {

  def props(appName: String,
            consumerKey: String,
            consumerSecret: String,
            token: String,
            secret: String) = Props(new TwitterTrackerHbc(appName, consumerKey, consumerSecret, token, secret))
}

/**
 * Created by fabiofumarola on 11/06/15.
 */
class TwitterTrackerHbc(appName: String,
                        consumerKey: String,
                        consumerSecret: String,
                        token: String,
                        secret: String) extends Actor with ActorLogging {

  val auth = new OAuth1(consumerKey, consumerSecret,
    token, secret)

  val msgQueue = new LinkedBlockingQueue[String](100000)
  val eventQueue = new LinkedBlockingQueue[Event](1000)
  val endpoint = new StatusesFilterEndpoint()

  var client: Option[BasicClient] = None

  var users = Set.empty[Long]
  var terms = Set.empty[String]
  var languages = Set.empty[String]
  var workers = List.empty[ActorRef]

  var rate = 50 milliseconds

  //init a default worker
  addWorkerKafka()

  override def receive: Receive = stopped

  def stopped: Receive = {
    case Start =>
      setParameters()
      client = Some(basicClient())
      client.foreach(_.connect())

      if (client.forall(c => !c.isDone)) {
        sender() ! OperationAck(Start.toString, s"start listening on users $users and $terms with ${workers.size} workers")
        context.become(running)
      }
      else {
        sender() ! OperationFailed(Start.toString, s"the client was not started $client")
      }

    case Status =>
      sender() ! OperationAck(Status.toString, s"the service is stopped ${Stop}")

    case Track(usersT, termsT, langsT) =>
      users = users union usersT
      terms = terms union termsT
      languages = languages union langsT
      sender() ! Track(users, terms, languages)

    case AddWorkers(number) =>
      (0 until number).foreach(_ => addWorkerKafka())
      sender ! Workers(workers.size)

    case Workers(_) =>
      sender ! Workers(workers.size)

    case DelWorkers(number) =>
      removeWorkers(number)
      sender() ! Workers(workers.size)

  }

  def running: Receive = {

    case Stop =>
      client.foreach(_.stop())
      sender() ! OperationAck(Stop.toString, s"the tracker is in $Stop")
      context.become(stopped)

    case Restart =>
      setParameters()
      client.foreach(_.reconnect())
      sender() ! OperationAck(Restart.toString, "reconnected updating tracked terms and users")

    case Status =>
      sender() ! OperationAck(Status.toString, s"the service is ${Start}")

    case Track(usersT, termsT, langsT) =>
      users = users union usersT
      terms = terms union termsT
      languages = languages union langsT
      sender() ! Track(users, terms, languages)

    case ChangeWorkersRate(update) =>
      rate = update
      workers.foreach(_ ! TwitterWorkerPrint.UpdateRate(rate))

    case AddWorkers(number) =>
      (0 until number).foreach(_ => addWorkerKafka())
      sender ! Workers(workers.size)

    case Workers(_) =>
      sender ! Workers(workers.size)

    case DelWorkers(number) =>
      removeWorkers(number)
      sender() ! Workers(workers.size)
  }

  def basicClient(): BasicClient = {
    new ClientBuilder().name(appName)
      .hosts(new HttpHosts(STREAM_HOST))
      .authentication(auth)
      .endpoint(endpoint)
      .processor(new StringDelimitedProcessor(msgQueue))
      .eventMessageQueue(eventQueue)
      .build()
  }

  def setParameters(): Unit = {
    endpoint.removePostParameter(Constants.TRACK_PARAM)
    endpoint.addPostParameter(Constants.TRACK_PARAM, terms.mkString(","))
    endpoint.removePostParameter(Constants.FOLLOW_PARAM)
    endpoint.addPostParameter(Constants.FOLLOW_PARAM, users.mkString(","))
  }

  def addWorkerKafka(): Unit =
    workers ::= context.actorOf(TwitterWorkerKafka.props(msgQueue))

  def addWorkerPrint(): Unit =
    workers ::= context.actorOf(TwitterWorkerPrint.props(msgQueue))

  def removeWorkers(n: Int): Unit = {
    val size = if (n < workers.size - 1) n else workers.size - 1

    for (i <- 0 until size) {
      workers.head ! PoisonPill
      workers = workers.tail
    }
  }

}
