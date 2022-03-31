package mwang.online.bean

case class CovidDTO(
   dateId: String,
   provinceShortName: String,
   cityName: String,
   locationId: String,
   pid: String,
   statisticsData: String,
   currentConfirmedCount: Long,
   confirmedCount: Long,
   confirmedIncr: Long,
   suspectedCount: Long,
   curedCount: Long,
   deadCount: Long
)
