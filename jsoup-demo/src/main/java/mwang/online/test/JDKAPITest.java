package mwang.online.test;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @Description TODO
 * @Date 2022/3/27 0027 14:14
 * @Created by mwangli
 */
public class JDKAPITest {


    @Test
    public void testGet() throws Exception {
        // 1.获取URL
        URL url = new URL("http://www.itcast.cn/?username=xx");
        // 2.获取连接对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 3.设置请求参数
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        connection.setConnectTimeout(30000);
        // 4.获取数据
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder html = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            html.append(line).append("\n");
        }
        System.out.println(html);
        // 5.关闭资源
        reader.close();
        inputStream.close();
    }

    @Test
    public void testPost() throws Exception {
        // 1.获取URL
        URL url = new URL("http://www.itcast.cn/");
        // 2.获取连接对象
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 3.设置请求参数
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        connection.setConnectTimeout(30000);
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write("username=xx".getBytes(StandardCharsets.UTF_8));
        // 4.获取数据
        InputStream inputStream = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder html = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            html.append(line).append("\n");
        }
        System.out.println(html);
        // 5.关闭资源
        reader.close();
        inputStream.close();
    }

}
