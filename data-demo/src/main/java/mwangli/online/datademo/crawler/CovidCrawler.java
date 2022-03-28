package mwangli.online.datademo.crawler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import mwangli.online.datademo.bean.CovidDTO;
import mwangli.online.utils.HttpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author mwangli
 * @date 2022/3/28 10:15
 */
@Slf4j
public class CovidCrawler {

    @Test
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
//        log.info("数据解析结果：{}", jsonStr);
        // 4,解析省份数据
        List<CovidDTO> covidDTOS = JSON.parseArray(jsonStr, CovidDTO.class);
        covidDTOS.forEach(System.out::println);
    }
}
