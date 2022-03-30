package mwang.online.bean

case class CovidDTO(
     provinceName: String,
     provinceShortName: String,
     currentConfirmedCount: Int,
     confirmedCount: Int,
     suspectedCount: Int,
     curedCount: Int,
     deadCount: Int,
     comment: String,
     locationId: String,
     pid: String,
     statisticsData: String,
     statisticsDataList: List[CovidDTO],
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
