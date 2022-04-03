package mwang.online.controller;

import com.alibaba.fastjson.JSONObject;
import mwang.online.mapper.DataViewMapper;
import mwang.online.bean.CovidDTO;
import mwang.online.utils.DateUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mwangli
 * @date 2022/4/1 14:32
 */
@CrossOrigin
@RestController("/view")
public class DataViewController {

    @Resource
    private DataViewMapper dataViewMapper;

    @GetMapping("/v1")
    public List<JSONObject> getData1() {
        List<CovidDTO> data = dataViewMapper.getData2();
        return data.stream().map(o -> {
            JSONObject res = new JSONObject();
            res.put("name", o.getProvinceShortName());
            res.put("value", o.getCurrentConfirmedCount());
            res.put("dateId", o.getDateId());
            return res;
        }).collect(Collectors.toList());
    }

    @GetMapping("/v2")
    public List<JSONObject> getData2() {
        // 获取各个省份数据
        return dataViewMapper.getData2().stream().filter(o -> !"香港".equals(o.getProvinceShortName()))
                .map(o -> {
                    JSONObject result = new JSONObject();
                    result.put("省份简称", o.getProvinceShortName());
                    result.put("当前确诊人数", o.getCurrentConfirmedCount());
                    result.put("累计确诊人数", o.getConfirmedCount());
                    result.put("疑似病例人数", o.getSuspectedCount());
                    result.put("累计治愈人数", o.getCuredCount());
                    result.put("累计死亡人数", o.getDeadCount());
                    result.put("dateId", o.getDateId());
                    return result;
                }).collect(Collectors.toList());
    }

    @GetMapping("/v3")
    public JSONObject getData3() {
        // 获取最近30天的全国累计历史数据
        JSONObject result = new JSONObject();
        List<CovidDTO> data3 = dataViewMapper.getData3();
        List<CovidDTO> filterList = data3.stream().filter(this::in30Days).collect(Collectors.toList());
        List<String> dateIdList = filterList.stream().map(CovidDTO::getDateId).collect(Collectors.toList());
        result.put("dateIdList", dateIdList);
        List<Integer> confirmedIncrData = filterList.stream().map(CovidDTO::getConfirmedIncr).collect(Collectors.toList());
        result.put("confirmedIncrData", confirmedIncrData);
        List<Integer> confirmedCountData = filterList.stream().map(CovidDTO::getConfirmedCount).collect(Collectors.toList());
        result.put("confirmedCountData", confirmedCountData);
        List<Integer> suspectedCountData = filterList.stream().map(CovidDTO::getSuspectedCount).collect(Collectors.toList());
        result.put("suspectedCountData", suspectedCountData);
        List<Integer> curedCountData = filterList.stream().map(CovidDTO::getCuredCount).collect(Collectors.toList());
        result.put("curedCountData", curedCountData);
        List<Integer> deadCountData = filterList.stream().map(CovidDTO::getDeadCount).collect(Collectors.toList());
        result.put("deadCountData", deadCountData);
        return result;
    }

    @GetMapping("/v4")
    public List<JSONObject> getData4() {
        // 境外输入top10
        List<CovidDTO> data4 = dataViewMapper.getData4();
        return data4.stream().map(o -> {
            JSONObject res = new JSONObject();
            res.put("value", o.getConfirmedCount());
            res.put("name", o.getProvinceShortName());
            res.put("dateId", o.getDateId());
            return res;
        }).collect(Collectors.toList());
    }

    @GetMapping("/v5")
    public JSONObject getData5() {
        // 浙江省各个城市数据
        JSONObject result = new JSONObject();
        List<CovidDTO> data = dataViewMapper.getData5();
        List<String> cityNameList = data.stream().map(CovidDTO::getCityName).collect(Collectors.toList());
        result.put("cityNameList", cityNameList);
        List<Integer> currentConfirmedIncrData = data.stream().map(CovidDTO::getCurrentConfirmedCount).collect(Collectors.toList());
        result.put("currentConfirmedIncrData", currentConfirmedIncrData);
        List<Integer> confirmedCountData = data.stream().map(CovidDTO::getConfirmedCount).collect(Collectors.toList());
        result.put("confirmedCountData", confirmedCountData);
        List<Integer> suspectedCountData = data.stream().map(CovidDTO::getSuspectedCount).collect(Collectors.toList());
        result.put("suspectedCountData", suspectedCountData);
        List<Integer> curedCountData = data.stream().map(CovidDTO::getCuredCount).collect(Collectors.toList());
        result.put("curedCountData", curedCountData);
        List<Integer> deadCountData = data.stream().map(CovidDTO::getDeadCount).collect(Collectors.toList());
        result.put("deadCountData", deadCountData);
        result.put("dateId", data.stream().findFirst().orElse(new CovidDTO()).getDateId());
        return result;
    }

    @GetMapping("/v6")
    public List<JSONObject> getData6() {
        // 防疫物资实时数据
        return dataViewMapper.getData6().stream()
                .map(o -> {
                    JSONObject result = new JSONObject();
                    result.put("物资名称", o.get("name"));
                    result.put("采购", o.get("cg"));
                    result.put("下拨", o.get("xb"));
                    result.put("捐赠", o.get("jz"));
                    result.put("消耗", o.get("xh"));
                    result.put("需求", o.get("xq"));
                    result.put("库存", o.get("kc"));
                    result.put("updateTime", o.get("updateTime"));
                    return result;
                }).collect(Collectors.toList());
    }

    private boolean in30Days(CovidDTO data) {
        String dateId = data.getDateId();
        Date date = DateUtils.parse(dateId, "yyyyMMdd");
        Date now = new Date();
        Date pre30Date = DateUtils.getNextDate(now, -30);
        assert date != null;
        return date.getTime() >= pre30Date.getTime() && date.getTime() <= now.getTime();
    }
}
