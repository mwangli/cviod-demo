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

    @Select("select * from t_result1 where dateId = DATE_FORMAT(NOW(),'%Y%m%d')")
    List<CovidDTO> getData1();

    @Select("select * from t_result2 where dateId = DATE_FORMAT(NOW(),'%Y%m%d')")
    List<CovidDTO> getData2();

    @Select("select * from t_result3 order by dateId")
    List<CovidDTO> getData3();

    @Select("select * from t_result4 where dateId = DATE_FORMAT(NOW(),'%Y%m%d') order by confirmedCount desc limit 10")
    List<CovidDTO> getData4();

    @Select("select * from t_result5 where dateId = DATE_FORMAT(NOW(),'%Y%m%d')")
    List<CovidDTO> getData5();

    @Select("select * from t_result6")
    List<Map<String,Object>> getData6();
}
