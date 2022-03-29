package mwang.online.utils

import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.kafka010.OffsetRange

import java.sql.DriverManager

/**
 * @Description TODO
 * @Date 2022/3/29 0029 21:56
 * @Created by mwangli
 */
object OffsetUtils {
  // 从mysql中获取偏移量
  def getOffsetMap(groupId: String, topic: String): Map[TopicPartition, Long] = {
    // 1.加载驱动获取链接
    val connection = DriverManager.getConnection("jdbc:mysql://112.74.187.216:3306/bigdata?characterEncoding=UTF-8&useSSL=false","root","Root.123456")
    // 2.编写SQL
    val sql = "select `partition`,offset from t_offset where group_id = ? and topic = ?"
    // 3.创建预编译语句
    val statement = connection.prepareStatement(sql)
    // 4.设置执行参数
    statement.setString(1, groupId)
    statement.setString(2, topic)
    // 5.封装返回结果
    val rs = statement.executeQuery()
    var offsetMap = Map[TopicPartition, Long]()
    while (rs.next()) {
      val partition = rs.getInt("partition")
      val offset = rs.getInt("offset")
      offsetMap += new TopicPartition(topic, partition) -> offset
    }
    // 6.关闭资源
    rs.close()
    statement.close()
    connection.close()
    offsetMap
  }


  // 手动提交偏移量到mysql
  def saveOffsets(groupId: String, offsets: Array[OffsetRange]) = {
    // 1.加载驱动获取链接
    val connection = DriverManager.getConnection("jdbc:mysql://112.74.187.216:3306/bigdata?characterEncoding=UTF-8&useSSL=false","root","Root.123456")
    // 2.编写SQL
    val sql = "replace into t_offset(topic,`partition`,group_id,offset) values(?,?,?,?)"
    // 3.创建预编译语句
    val statement = connection.prepareStatement(sql)
    // 4.设置执行参数
    for (o <- offsets) {
      statement.setString(1, o.topic)
      statement.setInt(2, o.partition)
      statement.setString(3, groupId)
      statement.setLong(4, o.untilOffset)
      statement.execute()
    }
    // 5.关闭资源
    statement.close()
    connection.close()
  }

}
