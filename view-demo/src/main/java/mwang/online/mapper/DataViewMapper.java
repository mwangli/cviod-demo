package mwang.online.mapper;

import mwang.online.bean.CovidDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * @author mwangli
 * @date 2022/4/1 14:33
 */
@Mapper
public interface DataViewMapper {

    @Select("select * from t_result1 where dateId = (SELECT DISTINCT dateId FROM t_result3 ORDER BY dateId DESC LIMIT 1))")
    List<CovidDTO> getData1();

    @Select("select * from t_result2 where dateId = (SELECT DISTINCT dateId FROM t_result3 ORDER BY dateId DESC LIMIT 1)")
    List<CovidDTO> getData2();

    @Select("select * from t_result3 order by dateId")
    List<CovidDTO> getData3();

    @Select("select * from t_result4 where dateId = (SELECT DISTINCT dateId FROM t_result3 ORDER BY dateId DESC LIMIT 1) limit 10")
    List<CovidDTO> getData4();

    @Select("select * from t_result5 where dateId = (SELECT DISTINCT dateId FROM t_result3 ORDER BY dateId DESC LIMIT 1)")
    List<CovidDTO> getData5();

    @Select("select * from t_result6")
    List<Map<String,Object>> getData6();
}
