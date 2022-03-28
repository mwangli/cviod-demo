package mwangli.online.datademo.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author mwangli
 * @date 2022/3/28 18:47
 */
@Component
public class MockData {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void mock() {

    }
}
