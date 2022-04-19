package mwang.online.mock;

import com.alibaba.fastjson.JSON;
import mwang.online.bean.ItemDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author mwangli
 * @date 2022/3/28 18:47
 */
@Component
@SpringBootTest
@RunWith(SpringRunner.class)
public class MockData {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private String[] itemName = new String[]{"N95口罩/个", "医用外科口罩/个", "84消毒液/瓶", "无接触测温枪/个", "一次性手套/副", "护目镜/副", "医用防护服/套"};
    private String[] itemFrom = new String[]{"采购", "下拨", "捐赠", "消耗", "需求"};

    @Test
    @Scheduled(cron = "0/5 * * * * ?")
    public void mock() {
        // 1.模拟生成物资数据
        Random random = new Random();
        for (int i = 0; i < 30; i++) {
            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setName(itemName[random.nextInt(itemName.length)]);
            itemDTO.setFrom(itemFrom[random.nextInt(itemFrom.length)]);
            itemDTO.setCount( random.nextInt(100)-50);
            // 2.将数据发送至Kafka
            kafkaTemplate.send("item_data", JSON.toJSONString(itemDTO));
        }
    }
}
