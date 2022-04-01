package mwang.online.controller;

import com.alibaba.fastjson.JSONObject;
import mwang.online.bean.DataViewVO;
import mwang.online.mapper.DataViewMapper;
import mwangli.online.datademo.bean.CovidDTO;
import mwangli.online.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<CovidDTO> getData1() {
        return dataViewMapper.getData1();
    }

    @GetMapping("/v2")
    public List<CovidDTO> getData2() {
        return dataViewMapper.getData2();
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
    public List<CovidDTO> getData4() {
        return dataViewMapper.getData4();
    }

    @GetMapping("/v5")
    public List<CovidDTO> getData5() {
        return dataViewMapper.getData5();
    }

    private boolean in30Days(CovidDTO data) {
        String dateId = data.getDateId();
        Date date = DateUtils.parse(dateId, "yyyyMMdd");
        Date now = new Date();
        Date pre30Date = DateUtils.getNextDate(now, -31);
        return date.getTime() >= pre30Date.getTime() && date.getTime() <= now.getTime();
    }
}
