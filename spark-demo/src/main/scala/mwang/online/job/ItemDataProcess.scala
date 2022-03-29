package mwang.online.job

/**
 *
 * @Date 2022/3/28 0028 23:04
 * @Created by mwangli
 */

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
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
    //    ssc.checkpoint("./ssc_check_point")
    // 2.Kafka配置
    val config = Map[String, Object](
      "bootstrap.servers" -> "node1:9092,node2:9092,node3:9092",
      "group.id" -> "sparkKafka",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit" -> (true: java.lang.Boolean),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer])
    val topics = Array("item_data")
    // 3.连接kafka
    val kafkaDS: InputDStream[ConsumerRecord[String, String]] = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, config))
    // 4.处理数据
    val value: DStream[String] = kafkaDS.map(_.value())
    value.print()
    // 5.启动任务等待结束
    ssc.start()
    ssc.awaitTermination()
  }
}


