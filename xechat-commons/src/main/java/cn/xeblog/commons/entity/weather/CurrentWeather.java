package cn.xeblog.commons.entity.weather;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 当前天气 实体类
 *
 * <p>注意：以和风天气的实体为准</p>
 *
 * @author nn200433
 * @date 2022-04-19 10:58:37
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather implements Serializable {

    /**
     * 温度，默认单位：摄氏度
     */
    private String temp;

    /**
     * 体感温度，默认单位：摄氏度
     */
    private String feelsLike;

    /**
     * 天气状况和图标的代码
     */
    private String icon;

    /**
     * 天气状况的文字描述，如：多云、阴、晴、雪......
     */
    private String text;

    /**
     * 风力
     */
    private String windPower;

    /**
     * 风向角度
     */
    private String wind360;

    /**
     * 风向
     */
    private String windDir;

    /**
     * 风力等级
     */
    private String winScale;

    /**
     * 风速，公里/小时
     */
    private String winSpeed;

    /**
     * 相对湿度，百分比数值
     */
    private String humidity;

    /**
     * 当前小时累计降水量，默认单位：毫米
     */
    private String precip;

    /**
     * 大气压强，默认单位：百帕
     */
    private String pressure;

    /**
     * 能见度，默认单位：公里
     */
    private String vis;

    /**
     * 云量，百分比数值。可能为空
     */
    private String cloud;

    /**
     * 露点温度。可能为空
     */
    private String dew;

}
