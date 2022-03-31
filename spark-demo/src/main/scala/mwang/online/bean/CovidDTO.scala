package mwang.online.bean

case class CovidDTO(
     provinceName: String,
     provinceShortName: String,
     currentConfirmedCount: Long,
     confirmedCount: Long,
     suspectedCount: Long,
     curedCount: Long,
     deadCount: Long,
     comment: String,
     locationId: String,
     pid: String,
     statisticsData: String,
     highDangerCount: String,
     midDangerCount: String,
     detectOrgCount: String,
     vaccinationOrgCount: String,
     cities: String,
     dangerAreas: String,
     cityName: String,
     currentConfirmedCountStr: String,
     dateId: String
)
