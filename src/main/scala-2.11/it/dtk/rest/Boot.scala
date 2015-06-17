package it.dtk.rest

import akka.actor.{ Props, ActorSystem }
import akka.io.IO
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern._

/**
 * Created by fabiofumarola on 14/06/15.
 */
object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("twitter-monitor")

  // create and start our service actor
  val service = system.actorOf(Props[TwitterMonitorActor], "demo-service")

  implicit val timeout = Timeout(5.seconds)

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 80)
}
