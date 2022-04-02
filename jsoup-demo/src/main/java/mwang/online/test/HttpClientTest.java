package mwang.online.test;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description TODO
 * @Date 2022/3/27 0027 14:40
 * @Created by mwangli
 */
public class HttpClientTest {

    @Test
    public void testGet() throws IOException {
        // 1.创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2.创建HttpGet请求并设置参数
        HttpGet httpGet = new HttpGet("http://www.itcast.cn/?username=xx");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        // 3.发起请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 4.获取响应数据
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, "UTF-8");
            System.out.println(html);
        }
        // 5.关闭资源
        response.close();
        httpClient.close();
    }

    @Test
    public void testPost() throws IOException {
        // 1.创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 2.创建HttpGet请求并设置参数
        HttpPost httpPost = new HttpPost("http://www.itcast.cn/");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", "xx"));
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(params, "UTF-8");
        httpPost.setEntity(formEntity);
        // 3.发起请求
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 4.获取响应数据
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, "UTF-8");
            System.out.println(html);
        }
        // 5.关闭资源
        response.close();
        httpClient.close();
    }

    @Test
    public void testPool() throws IOException {
        // 1.创建http连接管理器
        PoolingHttpClientConnectionManager pool = new PoolingHttpClientConnectionManager();
        // 2.设置最大连接数
        pool.setMaxTotal(200);
        // 3，设置每个主机的最大并发数
        pool.setDefaultMaxPerRoute(20);
        doGet(pool);
        doGet(pool);

    }

    private void doGet(PoolingHttpClientConnectionManager pool) throws IOException {
        // 1.从连接池中获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(pool).build();
        // 2.创建HttpClient对象
        HttpGet httpGet = new HttpGet("http://www.itcast.cn/?username=xx");
        // 3.发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 4.获取数据
        if (response.getStatusLine().getStatusCode() == 200) {
            HttpEntity entity = response.getEntity();
            String html = EntityUtils.toString(entity, "UTF-8");
            System.out.println(html);
        }
        // 5.关闭资源
        response.close();
    }

    @Test
    public void testConfig() throws Exception {
        // 0.创建请求配置对象
        RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间
                .setSocketTimeout(10000)
                // 设置创建连接超时时间
                .setConnectTimeout(10000)
                // 设置请求超时时间
                .setConnectionRequestTimeout(10000)
                // 设置代理 https://proxy.mimvp.com/freeopen
                // .setProxy(new HttpHost("163.125.157.188", 8888))
                .build();
        // 1.创建HttClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        // 2.创建HttGet对象
        HttpGet httpGet = new HttpGet("http://www.itcast.cn/?username=xx");
        // 3.发送请求
        CloseableHttpResponse response = httpClient.execute(httpGet);
        // 4.获取响应
        if (response.getStatusLine().getStatusCode() == 200) {
            String html = EntityUtils.toString(response.getEntity(), "UTF-8");
            System.out.println(html);
        }
        // 5.关闭资源
        response.close();
        httpClient.close();

    }
}
