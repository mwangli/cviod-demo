package mwang.online.bean

case class ProvinceDataDTO(
    provinceShortName: String,
    currentConfirmedCount: Int,
    currentConfirmedIncr: Int,
    confirmedCount: Int,
    confirmedIncr: Int,
    suspectedCount: Int,
    suspectedCountIncr: Int,
    curedCount: Int,
    curedIncr: Int,
    deadCount: Int,
    deadIncr: Int,
    dateId: String,
    locationId: String
)
