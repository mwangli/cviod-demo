package mwang.online.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @Description TODO
 * @Date 2022/3/27 0027 15:32
 * @Created by mwangli
 */
public class HttpUtils {

    private static PoolingHttpClientConnectionManager pool;

    private static List<String> userAgents;

    private static RequestConfig config;

    static {
        pool = new PoolingHttpClientConnectionManager();
        pool.setMaxTotal(20);
        pool.setDefaultMaxPerRoute(20);
        config = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(10000)
                .setConnectionRequestTimeout(10000)
                .build();
        userAgents = Arrays.asList("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
                "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1",
                "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11",
                "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_0) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon 2.0)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; TencentTraveler 4.0)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; The World)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; SE 2.X MetaSr 1.0; SE 2.X MetaSr 1.0; .NET CLR 2.0.50727; SE 2.X MetaSr 1.0)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; 360SE)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Avant Browser)",
                "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");
    }

    public static String getHtml(String url) {
        // 1.从连接池中获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pool).build();
        // 2.创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        // 3.设置请求头和其他配置
        httpGet.setConfig(config);
        httpGet.setHeader("User-Agent", userAgents.get(new Random().nextInt(userAgents.size())));
        // 4.发送请求
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // 5.获取数据
            if (response.getStatusLine().getStatusCode() == 200) {
                if (response.getEntity() != null) {
                    return EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
//        String html = HttpUtils.getHtml("http://www.itcast.cn/?username=xx");
        String html = HttpUtils.getHtml("https://ncov.dxy.cn/ncovh5/view/pneumonia");
        System.out.println(html);
    }
}


