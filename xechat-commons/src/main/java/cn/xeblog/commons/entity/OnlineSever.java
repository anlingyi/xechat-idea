package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 鱼塘服务器
 *
 * @author nn200433
 * @date 2022-07-12 012 08:11:58
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OnlineSever {

    /**
     * 鱼塘名字
     */
    private String name;

    /**
     * 鱼塘ip
     */
    private String ip;

    /**
     * 鱼塘端口
     */
    private Integer port;

}
