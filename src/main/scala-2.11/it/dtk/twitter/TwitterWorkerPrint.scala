package it.dtk.twitter

import java.util.concurrent.BlockingQueue
import akka.actor.{ Props, ActorLogging, Actor }
import scala.concurrent.duration._
import scala.language.postfixOps

object TwitterWorkerPrint {

  case class UpdateRate(rate: FiniteDuration)

  def props(queue: BlockingQueue[String]) = Props(new TwitterWorkerPrint(queue))
}

/**
 * Created by fabiofumarola on 11/06/15.
 */
class TwitterWorkerPrint(queue: BlockingQueue[String]) extends Actor with ActorLogging {
  import TwitterWorkerPrint._
  import context.dispatcher
  case object Process
  var rate = 50 milliseconds

  var cancellable = context.system.scheduler.scheduleOnce(rate, self, Process)

  override def receive: Receive = {

    case UpdateRate(update) =>
      rate = update

    case Process =>
      val tweet = queue.take()
      log.debug("processing a tweet {}", tweet.take(50))
      cancellable = context.system.scheduler.scheduleOnce(rate, self, Process)
  }

  override def postStop(): Unit = {
    cancellable.cancel()
  }
}
