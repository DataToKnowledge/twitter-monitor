package it.dtk.twitter

import it.dtk.AkkaWordSpec

/**
 * Created by fabiofumarola on 09/06/15.
 */
class TwitterTrackerHbcSpec extends AkkaWordSpec("BasicProducer") {

  import MessageProtocol._

  val config = system.settings.config.getConfig("producer.twitter")
  val appName = config.getString("name")
  val consumerKey = config.getString("consumerKey")
  val consumerSecret = config.getString("consumerSecret")
  val token = config.getString("token")
  val secret = config.getString("secret")

  "A TwitterTrackerHbc " when {
    val tracker = system.actorOf(TwitterTrackerHbc.props(appName, consumerKey, consumerSecret, token, secret))
    "created " should {
      "result stopped " in {
        tracker ! Status
        expectMsg(OperationAck(Stop, s"the service is stopped ${Stop}"))
      }
    }
    val terms = Set("bari", "news", "croncaca")
    s"the terms $terms are add" should {
      "have 3 terms tracked" in {
        val msg = TrackTerms(terms)
        tracker ! msg
        expectMsg(MonitoringAck(msg, s"currently are tracked ${terms.size} terms"))
      }
    }

    val terms2 = Set("bari", "news")
    s"the terms $terms2 are add" should {
      "have 3 terms tracked" in {
        val msg = TrackTerms(terms2)
        tracker ! msg
        expectMsg(MonitoringAck(msg, s"currently are tracked 3 terms"))
      }
    }

    val terms3 = Set("milano", "furto")
    s"the terms $terms3 are add" should {
      "have 5 terms tracked" in {
        val msg = TrackTerms(terms3)
        tracker ! msg
        expectMsg(MonitoringAck(msg, s"currently are tracked 5 terms"))
      }
    }

    val users = Set(1L, 2L, 3L)
    s"the users $users are add" should {
      "track 3 users" in {
        val msg = TrackUsers(users)
        tracker ! msg
        expectMsg(MonitoringAck(msg, s"currently are tracked ${users.size} users"))
      }
    }
  }

  "A TwitterTrackerHbc" when {
    val tracker = system.actorOf(TwitterTrackerHbc.props(appName, consumerKey, consumerSecret, token, secret))

    val terms = Set("bari", "news", "croncaca")
    s"started with $terms terms" should {

      val termMsg = TrackTerms(terms)
      tracker ! termMsg
      expectMsg(MonitoringAck(termMsg, s"currently are tracked ${terms.size} terms"))

      tracker ! Start
      "return a ack with result" in {
        expectMsg(OperationAck(Start, s"start listening on users Set() and $terms with 1 workers"))
      }

      "the status should be running" in {
        tracker ! Status
        expectMsg(OperationAck(Stop, s"the service is stopped ${Start}"))
      }

      "succesfully stopped" in {
        tracker ! Stop
        expectMsg(OperationAck(Stop, "the tracker is stopped"))
      }

    }
  }

}
