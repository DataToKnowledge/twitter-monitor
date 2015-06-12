package it.dtk.twitter

import java.util.concurrent.LinkedBlockingQueue

import akka.actor.{ ActorRef, ActorLogging, Actor, Props }
import MessageProtocol._
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants._
import com.twitter.hbc.core.{ Constants, HttpHosts }
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.OAuth1
import scala.concurrent.duration._

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
  addWorker()

  override def receive: Receive = stopped

  def stopped: Receive = {
    case Start =>
      setParameters()
      client = Some(basicClient())
      client.foreach(_.connect())

      if (client.forall(c => !c.isDone)) {
        sender() ! OperationAck(Start, s"start listening on users $users and $terms with ${workers.size} workers")
        context.become(running)
      }
      else {
        sender() ! OperationFailed(Start, s"the client was not started $client")
      }

    case Status =>
      sender() ! OperationAck(Stop, s"the service is stopped ${Stop}")

    case u @ TrackUsers(set) =>
      users = users union set
      sender() ! MonitoringAck(u, s"currently are tracked ${users.size} users")

    case t @ TrackTerms(set) =>
      terms = terms union set
      sender() ! MonitoringAck(t, s"currently are tracked ${terms.size} terms")

    case l @ TrackLanguages(set) =>
      languages = languages union set
      sender() ! MonitoringAck(l, s"currently tracked ${languages.size} languages")

  }

  def running: Receive = {

    case Stop =>
      client.foreach(_.stop())
      sender() ! OperationAck(Stop, "the tracker is stopped")
      context.become(stopped)

    case Restart =>
      setParameters()
      client.foreach(_.reconnect())
      sender() ! OperationAck(Restart, "reconnected updating tracked terms and users")

    case Status =>
      sender() ! OperationAck(Stop, s"the service is stopped ${Start}")

    case u @ TrackUsers(set) =>
      users = users union set
      sender() ! MonitoringAck(u, s"currently are tracked ${users.size} users")

    case t @ TrackTerms(set) =>
      terms = terms union set
      sender() ! MonitoringAck(t, s"currently are tracked ${terms.size} terms")

    case l @ TrackLanguages(set) =>
      languages = languages union set
      sender() ! MonitoringAck(l, s"currently tracked ${languages.size} languages")

    case ChangeWorkersRate(update) =>
      rate = update
      workers.foreach(_ ! TwitterWorkerHbc.UpdateRate(rate))

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

  def addWorker(): Unit = workers :+= context.actorOf(TwitterWorkerHbc.props(msgQueue))

}
