package mwangli.online.datademo;

import mwangli.online.utils.DateUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class DataDemoApplication {

    public static void main(String[] args) {
//        SpringApplication.run(DataDemoApplication.class, args);


        String format = DateUtils.format(new Date().getTime(), "yyyy-MM-dd");
        System.out.println(format);
    }


}
