package mwang.online.utils

import org.apache.spark.sql.{ForeachWriter, Row}

import java.sql.{Connection, DriverManager, PreparedStatement}

abstract class BaseJdbcSink(sql: String) extends ForeachWriter[Row] {

  var connection: Connection = _
  var statement: PreparedStatement = _

  def doProcess(string: String, row: Row)

  override def open(partitionId: Long, version: Long): Boolean = {
    connection = DriverManager.getConnection("jdbc:mysql://112.74.187.216:3306/bigdata?characterEncoding=UTF-8&useSSL=false", "root", "Root.123456")
    true
  }

  override def process(value: Row): Unit = {
    doProcess(sql, value)
  }

  override def close(errorOrNull: Throwable): Unit = {
    if (connection != null) {
      connection.close()
    }
    if (statement != null) {
      statement.close()
    }
  }
}
