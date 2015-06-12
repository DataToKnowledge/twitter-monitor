package examples

import java.util.concurrent.LinkedBlockingQueue

import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.event.Event
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.core.{Constants, HttpHosts}
import com.twitter.hbc.httpclient.auth.OAuth1

import scala.collection.JavaConverters._


object TwitterHBCTest extends App {

  val consumerKey = ""
  val consumerSecret = ""
  val token = ""
  val secret = ""

  val msgQueue = new LinkedBlockingQueue[String](100000);
  val eventQueue = new LinkedBlockingQueue[Event](1000);

  val hosts = new HttpHosts(Constants.STREAM_HOST)
  val endpoint = new StatusesFilterEndpoint()
  val terms = List("Cristoforetti")
  endpoint.trackTerms(terms.asJava)

  val auth = new OAuth1(consumerKey, consumerSecret, token, secret)

  val client = new ClientBuilder()
    .name("test")
    .hosts(hosts)
    .authentication(auth)
    .endpoint(endpoint)
    .processor(new StringDelimitedProcessor(msgQueue))
    .eventMessageQueue(eventQueue)
    .build()

  client.connect()

  var i = 0

  val run1 = new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        val msg = msgQueue.take()
        println("From Thread 1 " + msg)
      }
    }
  })

  val run2 = new Thread(new Runnable {
    override def run(): Unit = {
      while (true) {
        val msg = msgQueue.take()
        println("From Thread 2 " + msg)
      }
    }
  })

  Thread.sleep(10000)

  run1.start()
  run2.start()

  Thread.sleep(20000)

  endpoint.trackTerms((terms ++ List("#cronaca", "#bari", "#news")).asJava)
  client.reconnect()
}
