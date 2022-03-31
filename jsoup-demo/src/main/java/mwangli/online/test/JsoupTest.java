package mwangli.online.test;

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

/**
 * @Description TODO
 * @Date 2022/3/27 0027 16:05
 * @Created by mwangli
 */
public class JsoupTest {

    @Test
    public void testGetDocument() throws IOException {
        // Document document = Jsoup.connect("http://www.itcast.cn/?username=xx").get();
        Document document = Jsoup.parse(new URL("http://www.itcast.cn/?username=xx"), 10000);
         System.out.println(document);
        Elements title = document.getElementsByTag("title");
        System.out.println(title.text());
    }

    @Test
    public void testGetElement() throws IOException {
        // 获取文档
        Document document = Jsoup.connect("http://www.itcast.cn/?username=xx").get();
        System.out.println(document);
        // 根据ID获取元素
        Element element = document.getElementById("qxzxqm");
        System.out.println(element);
        // 根据标签获取多个元素
        Elements elements = document.getElementsByTag("h2");
        Element first = elements.first();
        System.out.println(first);
        System.out.println(elements.last());
        // 根据样式获取元素
        Elements e = document.getElementsByClass("innr");
        System.out.println(e);
        // 根据属性获取元素
        Elements src = document.getElementsByAttribute("src");
        System.out.println(src.first());
    }

    @Test
    public void testElementOperate() throws IOException {
        Document document = Jsoup.connect("http://www.itcast.cn/?username=xx").get();
        Element element = document.getElementsByAttribute("src").get(20);
        System.out.println(element);
        // 获取元素id
        System.out.println(element.id());
        // 获取元素class
        System.out.println(element.className());
        // 获取元素中的属性值
        System.out.println(element.attr("src"));
        // 获取元素中的所有属性
        System.out.println(element.attributes().toString());
        // 获取元素文本内容
        System.out.println(element.text());
    }

    @Test
    public void testSelector() throws IOException {
        Document document = Jsoup.connect("http://www.itcast.cn/?username=xx").get();
        // 根据标签名获取元素
        Element span = document.select("span").last();
        System.out.println(span);
        // 根据id获取元素
        Elements select = document.select("#people")    ;
        System.out.println(select.text());
        // 根据class获取元素
        Elements ele = document.select(".innr");
        System.out.println(ele.text());
        // 根据属性获取元素
        Element e = document.select("[src]").last();
        System.out.println(e);
        // 根据属性值获取元素
        Elements ce = document.select("[class=innr]");
        System.out.println(ce);
    }

    @Test
    public void testSelector2() throws IOException {
        Document document = Jsoup.connect("http://www.itcast.cn/?username=xx").get();
        // 根据标签和id获取元素
        Elements e1 = document.select("span#people");
        System.out.println(e1);
        // 根据标签和class获取元素
        Elements e2 = document.select("li.xq_ty");
        System.out.println(e2);
        // 根据标签+class+属性获取元素
        Elements e3 = document.select("img[alt=宿迁传智互联网中等职业技术学校]");
        System.out.println(e3);
        // 查找某个元素下的直接子元素
        Elements e4 = document.select("div.head_up li");
        System.out.println(e4);
        // 查找某个元素下所有子元素
        Elements e5 = document.select("div.head_up>*");
        System.out.println(e5);
    }

}
