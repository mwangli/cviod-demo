package mwang.online.job

/**
 *
 * @Date 2022/3/28 0028 23:04
 * @Created by mwangli
 */

import mwang.online.utils.OffsetUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

object ItemDataProcess {

  def main(args: Array[String]): Unit = {
    // 1.准备环境
    val conf = new SparkConf()
      .setAppName("ItemDataProcess")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))
    val groupId = "sparkKafka"
    val topic = "item_data"
    //    ssc.checkpoint("./ssc_check_point")
    // 2.Kafka配置
    val config = Map[String, Object](
      "bootstrap.servers" -> "node1:9092,node2:9092,node3:9092",
      "group.id" -> groupId,
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (false: java.lang.Boolean),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer])
    val topics = Array(topic)
    // 从mysql中获取偏移量
    val offsetMap: Map[TopicPartition, Long] = OffsetUtils.getOffsetMap(groupId, topic)
    val kafkaDS: InputDStream[ConsumerRecord[String, String]] = if (offsetMap.nonEmpty) {
      println("mysql记录了offset信息，从offset处开始消费")
      // 3.连接kafka
      KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, config, offsetMap))
    } else {
      println("mysql没有记录offset信息，从latest处开始消费")
      // 3.连接kafka
      KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe[String, String](topics, config))
    }
    // 4.处理数据
    kafkaDS.foreachRDD(rdd => {
      if (rdd.count() > 0) {
        rdd.foreach(println(_))
        // 获取偏移量
        val offsets = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
        for (o <- offsets) {
          println(s"topic=${o.topic},formOffset=${o.fromOffset},untilOffset=${o.untilOffset}")
        }
        // 提交偏移量到mysql
        OffsetUtils.saveOffsets(groupId, offsets)
      }
    })
    // 5.启动任务等待结束
    ssc.start()
    ssc.awaitTermination()
  }
}


