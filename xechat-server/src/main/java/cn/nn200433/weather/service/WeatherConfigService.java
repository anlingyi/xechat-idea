package cn.nn200433.weather.service;


import cn.xeblog.commons.entity.weather.WeatherConfig;

/**
 * 天气配置服务
 *
 * @author nn200433
 * @date 2022-04-24 02:00:42
 */
public interface WeatherConfigService {

    /**
     * 构建配置
     *
     * @return {@link WeatherConfig }
     * @author nn200433
     */
    public WeatherConfig buildConfig();

}
