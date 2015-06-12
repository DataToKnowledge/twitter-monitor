package it.dtk.twitter

import java.util.concurrent.BlockingQueue
import akka.actor.{ Props, ActorLogging, Actor }
import it.dtk.kafka.KafkaProducer
import scala.concurrent.duration._

object TwitterWorkerHbc {

  case class UpdateRate(rate: FiniteDuration)

  def props(queue: BlockingQueue[String]) = Props(new TwitterWorkerHbc(queue))
}

/**
 * Created by fabiofumarola on 11/06/15.
 */
class TwitterWorkerHbc(queue: BlockingQueue[String]) extends Actor with ActorLogging {
  import TwitterWorkerHbc._
  import context.dispatcher
  case object Process
  var rate = 50 milliseconds

  val config = context.system.settings.config.atPath("producer.kafka")
  val topic = config.getString("topic")
  val brokers = config.getString("brokers")
  val kafkaProducer = new KafkaProducer(topic, brokers)

  var cancellable = context.system.scheduler.scheduleOnce(rate, self, Process)

  override def receive: Receive = {

    case UpdateRate(update) =>
      rate = update

    case Process =>
      val tweet = queue.take()
      log.debug("processing a tweet {}", tweet.take(50))
      kafkaProducer.send(tweet)
      cancellable = context.system.scheduler.scheduleOnce(rate, self, Process)
  }

  override def postStop(): Unit = {
    cancellable.cancel()
  }
}
