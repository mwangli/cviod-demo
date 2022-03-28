package mwang.online.job


import java.lang.reflect.Array

/**
 *
 * @Date 2022/3/28 0028 23:04
 * @Created by mwangli
 */
object ItemDataProcess {

  def main(args: Array[String]): Unit = {
    // 1.准备SparkStreaming环境
    val value = new SaprkConf().set
    new SparkContext()
    // 2.配置kafka连接参数

    // 3.连接kafka获取消息
    // 4.实时处理数据
    // 5.将处理结果存入mysql
    // 6.开启SparkStreaming任务


  }

}
