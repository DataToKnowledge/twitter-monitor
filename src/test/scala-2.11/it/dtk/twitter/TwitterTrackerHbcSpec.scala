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
        expectMsg(OperationAck(Stop.toString, s"the service is stopped ${Stop}"))
      }
    }
    val terms = Set("bari", "news", "croncaca")
    s"the terms $terms are add" should {
      "have 3 terms tracked" in {
        val msg = Track(terms = terms)
        tracker ! msg
        expectMsg(Track(terms = terms))
      }
    }

  }

  "A TwitterTrackerHbc" when {
    val tracker = system.actorOf(TwitterTrackerHbc.props(appName, consumerKey, consumerSecret, token, secret))

    val terms = Set("bari", "news", "croncaca")
    s"started with $terms terms" should {

      val termMsg = Track(terms = terms)
      tracker ! termMsg
      expectMsg(termMsg)

      tracker ! Start
      "return a ack with result" in {
        expectMsg(OperationAck(Start.toString, s"start listening on users Set() and $terms with 1 workers"))
      }

      "the status should be running" in {
        tracker ! Status
        expectMsg(OperationAck(Stop.toString, s"the service is stopped ${Start}"))
      }

      "succesfully stopped" in {
        tracker ! Stop
        expectMsg(OperationAck(Stop.toString, "the tracker is stopped"))
      }

    }
  }

}
