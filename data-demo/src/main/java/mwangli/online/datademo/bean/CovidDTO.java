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
    private Integer currentConfirmedCount;
    private Integer confirmedCount;
    private Integer confirmedIncr;
    private Integer suspectedCount;
    private Integer curedCount;
    private Integer deadCount;
    private String locationId;
    private String pid;
    private String statisticsData;
    private String cities;
    private String cityName;
    private String dateId;
}
