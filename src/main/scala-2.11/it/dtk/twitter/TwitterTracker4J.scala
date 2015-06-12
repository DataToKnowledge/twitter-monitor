package it.dtk.twitter

import akka.actor.{ActorRef, ActorLogging, Actor, Props}
import twitter4j._
import twitter4j.conf.ConfigurationBuilder


object TwitterTracker4J {

  def props() = Props(new TwitterTracker4J)
}

/**
 * Created by fabiofumarola on 11/06/15.
 */
class TwitterTracker4J extends Actor with ActorLogging {
  import MessageProtocol._

  val config = context.system.settings.config.getConfig("producer.twitter")

  val twitterConf = new ConfigurationBuilder()

  twitterConf.setDebugEnabled(true)
    .setOAuthConsumerKey(config.getString("consumerKey"))
    .setOAuthConsumerSecret(config.getString("consumerSecret"))
    .setOAuthAccessToken(config.getString("token"))
    .setOAuthAccessTokenSecret(config.getString("secret"))

  val stream = new TwitterStreamFactory((twitterConf.build())).getInstance()
  stream.addListener(statusListener(self))

  var users = Set.empty[Long]
  var terms = Set.empty[String]
  var languages = Set.empty[String]

  override def receive: Receive = stopped

  def stopped: Receive = {

    case Start =>
      stream.filter(filterQuery())
      sender() ! OperationAck(Start, s"start listening on users $users and tahs $terms")
      context.become(running)

    case Status =>
      sender() ! OperationAck(Stop, s"the service is stopped ${Stop}")

    case u @ TrackUsers(set) =>
      users = users.union(set)
      sender() ! MonitoringAck(u, s"currently are tracked ${users.size} users")

    case h @ TrackTerms(set) =>
      terms = terms.union(set)
      sender() ! MonitoringAck(h, s"currently are tracked ${terms.size} tags")

    case l @ TrackLanguages(set) =>
      languages = languages union set
      sender() ! MonitoringAck(l, s"currently tracked ${languages.size} languages")
  }

  def running: Receive = {

    case Stop =>
      stream.cleanUp()
      context.become(stopped)
      sender() ! OperationAck(Stop, "Twitter Tracker stopped")

    case Restart =>
      stream.cleanUp()
      stream.filter(filterQuery())
      sender() ! OperationAck(Stop, "Twitter Tracker restarted")

    case Status =>
      sender() ! OperationAck(Start, s"the service is in ${Start}")

    case u @ TrackUsers(set) =>
      users = users.union(set)
      sender() ! MonitoringAck(u, s"currently are tracked ${users.size} users")

    case h @ TrackTerms(set) =>
      terms = terms.union(set)
      sender() ! MonitoringAck(h, s"currently are tracked ${terms.size} tags")

    case l @ TrackLanguages(set) =>
      languages = languages union set
      sender() ! MonitoringAck(l, s"currently tracked ${languages.size} languages")

    case Stall(msg) =>
      println(msg.getMessage)

    case Deletion(msg) =>
      println(msg.getStatusId)

    case ScrubGeo(id, upToStatusId) =>
      println(s"weir message for user $id and status id $upToStatusId")

    case StatusM(status) =>
      println(s"message from user ${status.getUser.getName} with message ${status.getText} ")

    case LimitatedStatus(count) =>
      println(s"limited status count $count")

    case Error(ex) =>
      ex.printStackTrace()
  }

  def filterQuery() =
    new FilterQuery(0, users.toArray, terms.toArray, null, languages.toArray)

  def statusListener(sender: ActorRef) = new StatusListener {
    override def onStallWarning(warning: StallWarning): Unit = sender ! Stall(warning)

    override def onDeletionNotice(statusDeletionNotice: StatusDeletionNotice): Unit =
      sender ! Deletion(statusDeletionNotice)

    override def onScrubGeo(userId: Long, upToStatusId: Long): Unit =
      sender ! ScrubGeo(userId, upToStatusId)

    override def onStatus(status: Status): Unit = sender ! StatusM(status)

    override def onTrackLimitationNotice(numberOfLimitedStatuses: Int): Unit =
      sender ! LimitatedStatus(numberOfLimitedStatuses)

    override def onException(ex: Exception): Unit = sender ! Error(ex)

  }

  sealed trait Message
  case class Stall(warning: StallWarning) extends Message
  case class Deletion(deletion: StatusDeletionNotice) extends Message
  case class ScrubGeo(userId: Long, upToStatusId: Long) extends Message
  case class StatusM(status: Status) extends Message
  case class LimitatedStatus(count: Int) extends Message
  case class Error(ex: Exception) extends Message
}
