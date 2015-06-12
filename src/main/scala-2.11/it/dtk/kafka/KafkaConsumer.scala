package it.dtk.kafka

import java.util.Properties
import kafka.consumer.{ Whitelist, Consumer, ConsumerConfig }
import kafka.serializer.DefaultDecoder

/**
 * Created by fabiofumarola on 12/06/15.
 */
class KafkaConsumer(
    val topic: String,
    val groupId: String,
    val zookeepersList: String,
    val readFromStart: Boolean = true) {

  private val props = new Properties()
  props.put("group.id", groupId)
  props.put("zookeeper.connect", zookeepersList)
  props.put("auto.offset.reset", if (readFromStart) "smallest" else "largest")

  private val config = new ConsumerConfig(props)
  private val connector = Consumer.create(config)
  private val topicFilter = Whitelist(topic)
  private val decoder = new DefaultDecoder()

  //create 1 stream and get it by head operator
  val inStream = connector.createMessageStreamsByFilter(topicFilter, 1, decoder, decoder).head

  def read(writer: (Array[Byte]) => Unit): Unit = {
    inStream.foreach(msg => writer(msg.message()))
  }

  def close(): Unit = connector.shutdown()

}
