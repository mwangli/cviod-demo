package mwang.online.job

import com.alibaba.fastjson.JSON
import mwang.online.bean.CovidDTO
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

object CovidDataProcessJob {

  def main(args: Array[String]): Unit = {
    // 0.创建StructStreaming环境
    val sparkSession: SparkSession = SparkSession.builder().master("local[*]")
      .appName("CovidDataProcess").getOrCreate()
    val sc: SparkContext = sparkSession.sparkContext
    sc.setLogLevel("WARN")

    import sparkSession.implicits._
    import org.apache.spark.sql.functions._

    // 1.连接kafka
    val kafkaDF: DataFrame = sparkSession.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "node1:9092,node2:9092,node3:9092")
      .option("subscribe", "city_data").load()
    // 2.处理数据
    val jsonStrDS: Dataset[String] = kafkaDF.selectExpr("CAST(value as STRING)").as[String]
    //    jsonStrDS.writeStream.format("console").outputMode("append")
    //      .trigger(Trigger.ProcessingTime(0)).option("truncate", value = false)
    //      .start().awaitTermination()
    val covidDS = jsonStrDS.map(json => {
      JSON.parseObject(json, classOf[CovidDTO])
    })
    // 获取省份数据
    covidDS.filter((city)==>)
    // 3.聚合数据
    // 3.保存结果
  }

}
