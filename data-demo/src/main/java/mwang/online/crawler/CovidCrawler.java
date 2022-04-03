package mwang.online.crawler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import mwang.online.utils.HttpUtils;
import mwang.online.bean.CovidDTO;
import mwang.online.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author mwangli
 * @date 2022/3/28 10:15
 */
@Slf4j
@Component
@SpringBootTest
@RunWith(SpringRunner.class)
public class CovidCrawler {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    @Scheduled(cron = " 0 5 0 * * ?")
//    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 60 * 60 * 24)
    public void crawlCovidData() {
        // 1.请求指定页面
        String html = HttpUtils.getHtml("https://ncov.dxy.cn/ncovh5/view/pneumonia");
        // 2.解析相应数据
        Document document = Jsoup.parse(html);
        String text = document.select("script[id=getAreaStat]").toString();
        // 3.正则获取json
        String regex = "\\[(.*)]";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(text);
        String jsonStr = "";
        if (matcher.find()) {
            String res = matcher.group(0);
            jsonStr += res;
        } else {
            log.error("数据解析异常");
        }
        // 4,解析省份数据
        List<CovidDTO> covidDTOS = JSON.parseArray(jsonStr, CovidDTO.class);
        String date = DateUtils.format(System.currentTimeMillis(), "yyyyMMdd");
        covidDTOS.forEach(province -> {
            province.setDateId(date);
            String cityStr = province.getCities();
            if (StringUtils.isNotEmpty(cityStr)) {
                // 5.解析城市数据
                List<CovidDTO> cities = JSON.parseArray(cityStr, CovidDTO.class);
                cities.forEach(city -> {
                    city.setDateId(date);
                    city.setPid(province.getLocationId());
                    city.setProvinceShortName(province.getProvinceShortName());
                    // 将城市数据发送到Kafka
                    kafkaTemplate.send("city_data", JSON.toJSONString(city));
                });
            }
            province.setCities(null);
            // 6.获取省份的历史数据,只取30条
//            String dataUrl = province.getStatisticsData();
//            String data = HttpUtils.getHtml(dataUrl);
//            JSONObject jsonObject = JSON.parseObject(data);
//            String dataStr = jsonObject.getString("data");
//            List<CovidDTO> dataList = JSON.parseArray(dataStr, CovidDTO.class);
//            Stream<CovidDTO> limitList = dataList.stream().sorted(Comparator.comparing(CovidDTO::getDateId).reversed());
//            limitList.forEach(o -> {
//                o.setLocationId(province.getLocationId());
//                o.setProvinceShortName(province.getProvinceShortName());
//                kafkaTemplate.send("city_data", JSON.toJSONString(o));
//            });
            // 将省份数据发送到Kafka
            kafkaTemplate.send("city_data", JSON.toJSONString(province));
        });
    }
}


