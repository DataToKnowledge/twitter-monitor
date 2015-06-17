package it.dtk.kafka

import java.util.UUID

import it.dtk._
import org.scalatest.prop.PropertyChecks

/**
 * Created by fabiofumarola on 12/06/15.
 */
class KafkaSpec extends BaseWordSpec with PropertyChecks {

  val topic = "prova"
  val brokers: String = "192.168.99.100:9092"
  val clientId: String = UUID.randomUUID().toString
  val isAsync: Boolean = true
  val compress: Boolean = true
  val batchSize: Integer = 200
  val messageRetries: Integer = 3
  val requireAck: Integer = -1

  "A simple producer" should {
    "send messages to consume" in {
      val producer = new KafkaProducer(topic, brokers)

      producer.send("test message")

    }
  }

//  "A Simple Producer and Consumer" should {
//    "send string to broker and consume that string back" in {
//      val testMessage = UUID.randomUUID().toString
//      val testTopic = UUID.randomUUID().toString
//      val groupId_1 = UUID.randomUUID().toString
//
//      var testStatus = false
//
//      info("starting a broker for testing")
//      //       val producer = new KafkaProducer(testTopic,)
//    }
//  }
}
