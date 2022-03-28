package mwangli.online.datademo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author mwangli
 * @date 2022/3/28 10:56
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CovidDTO {
    private String provinceName;
    private String provinceShortName;
    private String currentConfirmedCount;
    private String confirmedCount;
    private String suspectedCount;
    private String curedCount;
    private String deadCount;
    private String comment;
    private String locationId;
    private String pid;
    private String statisticsData;
    private List<CovidDTO> statisticsDataList;
    private String highDangerCount;
    private String midDangerCount;
    private String detectOrgCount;
    private String vaccinationOrgCount;
    private String cities;
    private String dangerAreas;
    private String cityName;
    private String currentConfirmedCountStr;
    private String datetime;
    private String dateId;
}
