package it.dtk.rest

import akka.actor.{ Actor, ActorRef }
import akka.util.Timeout
import it.dtk.twitter.MessageProtocol._
import it.dtk.twitter.TwitterTrackerHbc
import spray.httpx.SprayJsonSupport
import spray.httpx.marshalling.ToResponseMarshallable
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import akka.pattern._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration._

trait MyJsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val trackConversion = jsonFormat3(Track)
  implicit val operationAckConversion = jsonFormat2(OperationAck)
  implicit val operationFailedConversion = jsonFormat2(OperationFailed)
  implicit val operationWorkerConversion = jsonFormat1(Workers)
}

/**
 * Created by fabiofumarola on 12/06/15.
 */
trait TwitterMonitorService extends HttpService with MyJsonProtocol {
  implicit val timeout = Timeout(5.seconds)

  val serviceActor: ActorRef
  implicit def executor: ExecutionContextExecutor

  val routes = pathPrefix("api" / "v1") {
    trackRoute ~ operationRoute ~ workerRoute
  }

  def trackRoute = {
    path("monitor" / "track") {
      get {
        complete {
          (serviceActor ? Track()).mapTo[Track]
        }
      } ~
        post {
          entity(as[Track]) { track =>
            complete {
              (serviceActor ? track).mapTo[Track]
            }
          }
        }
    }
  }

  def workerRoute = {
    path("worker" / IntNumber) { number =>
      put {
        complete {
          (serviceActor ? AddWorkers(number)).mapTo[Workers]
        }
      } ~
        delete {
          complete {
            (serviceActor ? DelWorkers(number)).mapTo[Workers]
          }
        }
    } ~
      path("worker" / "list") {
        get {
          complete {
            (serviceActor ? Workers(0)).mapTo[Workers]
          }
        }
      }
  }

  def operationRoute = {
    path("operation" / "status") {
      get {
        complete {
          (serviceActor ? Status).mapTo[OperationAck]
        }
      }
    } ~
      path("operation" / "start") {
        (get | post) {
          complete {
            (serviceActor ? Start).mapTo[OperationAck]
          }
        }
      } ~
      path("operation" / "stop") {
        (get | post) {
          complete {
            (serviceActor ? Stop).mapTo[OperationAck]
          }
        }
      } ~
      path("operation" / "restart") {
        (get | post) {
          complete {
            (serviceActor ? Restart).mapTo[OperationAck]
          }
        }
      }
  }
}

class TwitterMonitorActor extends Actor with TwitterMonitorService {

  val config = context.system.settings.config.getConfig("twitter-monitor")
  val twitterConf = config.getConfig("twitter")

  override val serviceActor = context.actorOf(TwitterTrackerHbc.props(
    appName = twitterConf.getString("name"),
    consumerKey = twitterConf.getString("consumerKey"),
    consumerSecret = twitterConf.getString("consumerSecret"),
    token = twitterConf.getString("token"),
    secret = twitterConf.getString("secret")
  ))

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  def receive = runRoute(routes)

  override implicit def executor: ExecutionContextExecutor = context.dispatcher
}

