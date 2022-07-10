package cn.xeblog.server.entity;

import cn.xeblog.commons.entity.weather.FutureWeather;
import lombok.Data;

import java.util.List;

/**
 * 未来天气 响应实体类
 *
 * @author nn200433
 * @date 2022-04-19 03:44:56
 */
@Data
public class RespWeatherFuture {

    /**
     * API状态码
     */
    private String code;

    /**
     * 当前API的最近更新时间
     */
    private String updateTime;

    /**
     * 当前数据的响应式页面，便于嵌入网站或应用
     */
    private String fxLink;

    /**
     * 数据
     */
    private List<FutureWeather> daily;

}