package cn.xeblog.commons.entity.weather;

import lombok.Builder;
import lombok.Data;

/**
 * 天气配置
 *
 * @author nn200433
 * @date 2022-04-24 02:07:04
 */
@Data
@Builder
public class WeatherConfig {

    /**
     * 未来3天天气接口
     */
    public String weather3dApi;

    /**
     * 未来7天天气接口
     */
    public String weather7dApi;

    /**
     * 实时天气接口
     */
    public String weatherNowApi;

    /**
     * 实时空气质量接口
     */
    public String airNowApi;

    /**
     * 应用程序 key
     */
    public String appKey;

    /**
     * 应用程序 密钥
     */
    public String appSecret;

}