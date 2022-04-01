package mwang.online.controller;

import mwang.online.bean.DataViewVO;
import mwang.online.mapper.DataViewMapper;
import mwangli.online.datademo.bean.CovidDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
    public List<CovidDTO> getData3() {
        return dataViewMapper.getData3();
    }

    @GetMapping("/v4")
    public List<CovidDTO> getData4() {
        return dataViewMapper.getData4();
    }

    @GetMapping("/v5")
    public List<CovidDTO> getData5() {
        return dataViewMapper.getData5();
    }
}
