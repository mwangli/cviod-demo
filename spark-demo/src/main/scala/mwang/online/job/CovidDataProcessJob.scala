package mwang.online.job

import com.alibaba.fastjson.JSON
import mwang.online.bean.CovidDTO
import mwang.online.utils.{BaseJdbcSink, DateUtils}
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{DataFrame, Dataset, ForeachWriter, Row, SparkSession}

object CovidDataProcessJob {

  def main(args: Array[String]): Unit = {
    // 0.创建StructStreaming环境
    val sparkSession: SparkSession = SparkSession.builder()
      .config("spark.testing.memory","2147480000")
      .master("local[*]")
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
    val covidDS: Dataset[CovidDTO] = jsonStrDS.map(JSON.parseObject(_, classOf[CovidDTO]))
    // 获取省份数据
    val provinceDS: Dataset[CovidDTO] = covidDS.filter(_.cityName == null)
    // 获取城市数据
    val cityDS: Dataset[CovidDTO] = covidDS.filter(_.cityName != null)
    // 3.聚合数据
    // 当日城市全部数据
    val result1 = cityDS.select(
      Symbol("dateId"),
      Symbol("provinceShortName"),
      Symbol("cityName"),
      Symbol("currentConfirmedCount"),
      Symbol("confirmedCount"),
      Symbol("suspectedCount"),
      Symbol("curedCount"),
      Symbol("deadCount"))
    // 当日各省累计数据
    val result2 = provinceDS.filter(_.statisticsData != null)
      .select(
        Symbol("dateId"),
        Symbol("provinceShortName"),
        Symbol("currentConfirmedCount"),
        Symbol("confirmedCount"),
        Symbol("suspectedCount"),
        Symbol("curedCount"),
        Symbol("deadCount"))
    // 历史全国汇总数据
    val result3 = provinceDS.groupBy(Symbol("dateId"))
      .agg(sum(Symbol("currentConfirmedCount")) as "currentConfirmedCount",
        sum(Symbol("confirmedCount")) as "confirmedCount",
        sum(Symbol("suspectedCount")) as "suspectedCount",
        sum(Symbol("curedCount")) as "curedCount",
        sum(Symbol("deadCount")) as "deadCount")
    // 当日各省境外输入排行
    val result4 = cityDS.filter(_.cityName.contains("境外输入"))
      .groupBy(Symbol("dateId"), Symbol("pid"), Symbol("provinceShortName"))
      .agg(sum(Symbol("confirmedCount")) as "confirmedCount")
      .sort(Symbol("confirmedCount").desc)
    // 当日杭州数据
    val result5 = cityDS.filter(city => city.provinceShortName == "浙江" && !city.cityName.contains("待明确"))
      .select(Symbol("dateId"),
        Symbol("provinceShortName"),
        Symbol("cityName"),
        Symbol("currentConfirmedCount"),
        Symbol("confirmedCount"),
        Symbol("suspectedCount"),
        Symbol("curedCount"),
        Symbol("deadCount"))
    // 4.保存结果
    result1.writeStream.format("console").outputMode("append").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start()
    result1.writeStream.outputMode("append")
      .foreach(new BaseJdbcSink("replace into t_result1 values(?,?,?,?,?,?,?,?,?) ") {
        override def doProcess(sql: String, row: Row): Unit = {
          statement = connection.prepareStatement(sql)
          statement.setString(1, row.getAs[String]("dateId"))
          statement.setString(2, row.getAs[String]("provinceShortName"))
          statement.setString(3, row.getAs[String]("cityName"))
          statement.setLong(4, row.getAs[Long]("currentConfirmedCount"))
          statement.setLong(5, row.getAs[Long]("confirmedCount"))
          statement.setLong(6, row.getAs[Long]("suspectedCount"))
          statement.setLong(7, row.getAs[Long]("curedCount"))
          statement.setLong(8, row.getAs[Long]("deadCount"))
          statement.setString(9, DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
          statement.execute()
        }
      }).start()
    result2.writeStream.format("console").outputMode("append").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start()
    result2.writeStream.outputMode("append")
      .trigger(Trigger.ProcessingTime(0)).option("truncate", value = false)
      .foreach(new BaseJdbcSink("replace into t_result2 values(?,?,?,?,?,?,?,?) ") {
        override def doProcess(sql: String, row: Row): Unit = {
          statement = connection.prepareStatement(sql)
          statement.setString(1, row.getAs[String]("dateId"))
          statement.setString(2, row.getAs[String]("provinceShortName"))
          statement.setLong(3, row.getAs[Long]("currentConfirmedCount"))
          statement.setLong(4, row.getAs[Long]("confirmedCount"))
          statement.setLong(5, row.getAs[Long]("suspectedCount"))
          statement.setLong(6, row.getAs[Long]("curedCount"))
          statement.setLong(7, row.getAs[Long]("deadCount"))
          statement.setString(8, DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
          statement.execute()
        }
      }).start()
    result3.writeStream.format("console").outputMode("complete").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start()
    result3.writeStream.outputMode("complete")
      .trigger(Trigger.ProcessingTime(0)).option("truncate", value = false)
      .foreach(new BaseJdbcSink("replace into t_result3 values(?,?,?,?,?,?,?) ") {
        override def doProcess(sql: String, row: Row): Unit = {
          statement = connection.prepareStatement(sql)
          statement.setString(1, row.getAs[String]("dateId"))
          statement.setLong(2, row.getAs[Long]("currentConfirmedCount"))
          statement.setLong(3, row.getAs[Long]("confirmedCount"))
          statement.setLong(4, row.getAs[Long]("suspectedCount"))
          statement.setLong(5, row.getAs[Long]("curedCount"))
          statement.setLong(6, row.getAs[Long]("deadCount"))
          statement.setString(7, DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
          statement.execute()
        }
      }).start()
    result4.writeStream.format("console").outputMode("complete").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start()
    result4.writeStream.outputMode("complete")
      .trigger(Trigger.ProcessingTime(0)).option("truncate", value = false)
      .foreach(new BaseJdbcSink("replace into t_result4 values(?,?,?,?) ") {
        override def doProcess(sql: String, row: Row): Unit = {
          statement = connection.prepareStatement(sql)
          statement.setString(1, row.getAs[String]("dateId"))
          statement.setString(2, row.getAs[String]("provinceShortName"))
          statement.setLong(3, row.getAs[Long]("confirmedCount"))
          statement.setString(4, DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
          statement.execute()
        }
      }).start()
    result5.writeStream.format("console").outputMode("append").trigger(Trigger.ProcessingTime(0)).option("truncate", value = false).start()
    result5.writeStream.outputMode("append")
      .trigger(Trigger.ProcessingTime(0)).option("truncate", value = false)
      .foreach(new BaseJdbcSink("replace into t_result5 values(?,?,?,?,?,?,?,?,?) ") {
        override def doProcess(sql: String, row: Row): Unit = {
          statement = connection.prepareStatement(sql)
          statement.setString(1, row.getAs[String]("dateId"))
          statement.setString(2, row.getAs[String]("provinceShortName"))
          statement.setString(3, row.getAs[String]("cityName"))
          statement.setLong(4, row.getAs[Long]("currentConfirmedCount"))
          statement.setLong(5, row.getAs[Long]("confirmedCount"))
          statement.setLong(6, row.getAs[Long]("suspectedCount"))
          statement.setLong(7, row.getAs[Long]("curedCount"))
          statement.setLong(8, row.getAs[Long]("deadCount"))
          statement.setString(9, DateUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
          statement.execute()
        }
      }).start().awaitTermination()
  }
}
