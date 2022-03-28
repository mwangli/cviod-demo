package mwangli.online.datademo.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Description TODO
 * @Date 2022/3/28 0028 22:23
 * @Created by mwangli
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {

    private String name;
    private String from;
    private Integer count;
}

