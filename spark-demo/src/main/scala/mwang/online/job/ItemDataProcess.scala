package mwang.online.job

/**
 *
 * @Date 2022/3/28 0028 23:04
 * @Created by mwangli
 */

import com.alibaba.fastjson.JSON
import mwang.online.utils.{DateUtils, OffsetUtils}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{CanCommitOffsets, ConsumerStrategies, HasOffsetRanges, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}

import java.sql.DriverManager
import java.util.Date

object ItemDataProcess {

  def main(args: Array[String]): Unit = {
    // 1.准备环境
    val conf = new SparkConf()
      .setAppName("ItemDataProcess")
      .setMaster("local[*]")
    val sc = new SparkContext(conf)
    sc.setLogLevel("WARN")
    val ssc = new StreamingContext(sc, Seconds(5))
    ssc.checkpoint("./ssc_check_point")
    val groupId = "sparkKafka"
    val topic = "item_data"
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
    // 提交偏移量
    //    val offsets = kafkaDS.asInstanceOf[HasOffsetRanges].offsetRanges
    //    OffsetUtils.saveOffsets(groupId, offsets)
    // 4.处理实时数据
    // {"count":289,"from":"下拨","name":"N95口罩/个"}
    // =>(N95口罩/个,(采购500,下拨0,捐赠0,消耗0,需求0,库存500))
    val tupleDS: DStream[(String, (Int, Int, Int, Int, Int, Int))] = kafkaDS.map(record => {
      val json = record.value()
      val jsonObj = JSON.parseObject(json)
      val name = jsonObj.getString("name")
      val from = jsonObj.getString("from")
      val count = jsonObj.getInteger("count")
      from match {
        case "采购" => (name, (count, 0, 0, 0, 0, count))
        case "下拨" => (name, (0, count, 0, 0, 0, count))
        case "捐赠" => (name, (0, 0, count, 0, 0, count))
        case "消耗" => (name, (0, 0, 0, -count, 0, -count))
        case "需求" => (name, (0, 0, 0, 0, -count, -count))
      }
    })
    val updateFunc = (currentValues: Seq[(Int, Int, Int, Int, Int, Int)], historyValues: Option[(Int, Int, Int, Int, Int, Int)]) => {
      // 0.定义变量用来累加当前批次数据
      var currentCg: Int = 0
      var currentXb: Int = 0
      var currentJz: Int = 0
      var currentXh: Int = 0
      var currentXq: Int = 0
      var currentKc: Int = 0
      if (currentValues.nonEmpty) {
        // 1.取出当前批次数据
        for (value <- currentValues) {
          currentCg += value._1
          currentXb += value._2
          currentJz += value._3
          currentXh += value._4
          currentXq += value._5
          currentKc += value._6
        }
        // 2.取出历史数据
        var historyCg = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._1
        var historyXb = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._2
        var historyJz = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._3
        var historyXh = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._4
        var historyXq = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._5
        var historyKc = historyValues.getOrElse((0, 0, 0, 0, 0, 0))._6
        // 3.聚合当前数据和历史数据
        historyCg += currentCg
        historyXb += currentXb
        historyJz += currentJz
        historyXh += currentXh
        historyXq += currentXq
        historyKc += currentKc
        // 4.返回聚合结果
        Some((historyCg, historyXb, historyJz, historyXh, historyXq, historyKc))
      }
      else historyValues
    }
    // 数据聚合处理
    val resultDS = tupleDS.updateStateByKey(updateFunc)
    resultDS.print()
    // 5.将聚合结果存入mysql
    resultDS.foreachRDD(rdd => {
      rdd.foreach(data => {
        val connection = DriverManager.getConnection("jdbc:mysql://112.74.187.216:3306/bigdata?characterEncoding=UTF-8&useSSL=false", "root", "Root.123456")
        // 2.编写SQL
        val sql = "insert into item_data values(?,?,?,?,?,?,?,?)"
        // 3.创建预编译语句
        val statement = connection.prepareStatement(sql)
        // 4.设置执行参数
        statement.setString(1, data._1)
        statement.setInt(2, data._2._1)
        statement.setInt(3, data._2._2)
        statement.setInt(4, data._2._3)
        statement.setInt(5, data._2._4)
        statement.setInt(6, data._2._5)
        statement.setInt(7, data._2._6)
        val dateStr = DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss")
        statement.setString(8, dateStr)
        statement.executeUpdate()
      })
    })

    // 6.启动任务等待结束
    ssc.start()
    ssc.awaitTermination()
  }
}


