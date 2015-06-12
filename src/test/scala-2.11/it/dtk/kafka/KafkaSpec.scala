package it.dtk.kafka


import java.util.UUID

import it.dtk._
import org.scalatest.prop.PropertyChecks

/**
 * Created by fabiofumarola on 12/06/15.
 */
class KafkaSpec extends BaseWordSpec with PropertyChecks {

   "A Simple Producer and Consumer" should {
     "send string to broker and consume that string back" in {
       val testMessage = UUID.randomUUID().toString
       val testTopic = UUID.randomUUID().toString
       val groupId_1 = UUID.randomUUID().toString

       var testStatus = false

       info("starting a broker for testing")
//       val producer = new KafkaProducer(testTopic,)
     }
   }
}
