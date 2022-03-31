package mwang.online.job

import com.alibaba.fastjson.JSON
import mwang.online.bean.{CovidDTO, ProvinceDataDTO}
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
    import scala.collection.JavaConversions._

    // 1.连接kafka
    val kafkaDF: DataFrame = sparkSession.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "node1:9092,node2:9092,node3:9092")
      .option("subscribe", "city_data").load()
    // 2.处理数据
    val jsonStrDS: Dataset[String] = kafkaDF.selectExpr("CAST(value as STRING)").as[String]
    val covidDS: Dataset[CovidDTO] = jsonStrDS.map(JSON.parseObject(_, classOf[CovidDTO]))
    // 获取省份数据
    val provinceDS: Dataset[CovidDTO] = covidDS.filter(_.statisticsData != null)
    // 获取城市数据
    val cityDS: Dataset[CovidDTO] = covidDS.filter(_.statisticsData == null)
    // 获取省份历史数据
    val historyDS = provinceDS.flatMap(p => {
      val list = JSON.parseArray(p.statisticsData, classOf[ProvinceDataDTO])
      list
    })
    // 3.聚合数据
    // 全国汇总数据
    val result1 = provinceDS.groupBy('dateId)
      .agg(sum('currentConfirmedCount) as "currentConfirmedCount",
        sum('confirmedCount) as "confirmedCount",
        sum('suspectedCount) as "suspectedCount",
        sum('curedCount) as "curedCount",
        sum('deadCount) as "deadCount")
    // 各省累计数据
    val result2 = provinceDS.select('dateId, 'provinceShortName, 'currentConfirmedCount, 'confirmedCount, 'suspectedCount, 'curedCount, 'deadCount)
    // 全国趋势数据
    val result3 = historyDS.groupBy('dateId)
      .agg(sum('confirmedIncr) as "confirmedIncr",
        sum('confirmedCount) as "confirmedCount",
        sum('suspectedCount) as "suspectedCount",
        sum('curedCount) as "curedCount",
        sum('deadCount) as "deadCount")
    // 境外输入排行
    val result4 = cityDS.filter(_.cityName.contains("境外输入"))
      .groupBy('dateId, 'pid, 'provinceShortName)
      .agg(sum('confirmedCount) as "confirmedCount")
      .sort('confirmedCount.desc)
    // 城市累计排行
    val result5 = cityDS.filter(_.provinceShortName == "杭州")
      .select('dateId, 'provinceShortName, 'cityName, 'currentConfirmedCount, 'confirmedCount, 'suspectedCount, 'curedCount, 'deadCount)
//      .sort('confirmedCount.desc)
    // 4.保存结果
    result5.writeStream.format("console").outputMode("append").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start().awaitTermination()
  }
}
