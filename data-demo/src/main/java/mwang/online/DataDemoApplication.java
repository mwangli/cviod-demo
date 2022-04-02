package mwang.online;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DataDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataDemoApplication.class, args);
    }


}
