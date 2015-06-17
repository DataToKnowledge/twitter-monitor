package it.dtk.kafka

import java.util.{ Properties, UUID }

import org.apache.kafka.clients.producer.{ KafkaProducer => KProducer, ProducerRecord, Producer, ProducerConfig }

/**
 * @param requireAck =  0) which means that the producer never waits for an acknowledgement from the broker (the same behavior as 0.7).
 *     This option provides the lowest latency but the weakest durability guarantees (some data will be lost when a server fails).
 *  1) which means that the producer gets an acknowledgement after the leader replica has received the data. This option provides
 *     better durability as the client waits until the server acknowledges the request as successful (only messages that were
 *     written to the now-dead leader but not yet replicated will be lost).
 * -1) which means that the producer gets an acknowledgement after all in-sync replicas have received the data. This option
 *     provides the best durability, we guarantee that no messages will be lost as long as at least
 **/
class KafkaProducer(topic: String,
                    val brokersList: String,
                    val clientId: String = UUID.randomUUID().toString,
                    val isAsync: Boolean = true,
                    val compress: Boolean = true,
                    val batchSize: Integer = 200,
                    val messageRetries: Integer = 3,
                    val requireAck: Integer = -1) {

  private val props = new Properties()
  props.put("bootstrap.servers", brokersList)
  props.put("client.id", clientId)
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("batch.num.messages", batchSize.toString)
  props.put("message.send.max.retries", messageRetries.toString)
  props.put("request.required.acks", requireAck.toString)

  private val producer = new KProducer[String, String](props)

  def send(message: String, optPartition: Option[String] = None): Unit = {
    val kafkaMessage = optPartition
      .map(p => new ProducerRecord[String, String](topic, p, message))
      .getOrElse(new ProducerRecord[String, String](topic, message))

    producer.send(kafkaMessage)
  }

  def close(): Unit =
    producer.close()
}
