package cn.xeblog.server.service.impl;

import cn.xeblog.commons.constants.HeFengWeatherConstants;
import cn.xeblog.commons.entity.weather.WeatherConfig;
import cn.xeblog.server.service.WeatherConfigService;

/**
 * 和风天气配置服务实现
 *
 * @author nn200433
 * @date 2022-04-24 02:12:09
 */
public class HeFengWeatherConfigServiceImpl implements WeatherConfigService {

    public String appKey;

    public HeFengWeatherConfigServiceImpl(String key) {
        appKey = key;
    }

    @Override
    public WeatherConfig buildConfig() {
        return WeatherConfig.builder()
                .weatherNowApi(HeFengWeatherConstants.HE_FENG_WEATHER_NOW)
                .weather3dApi(HeFengWeatherConstants.HE_FENG_WEATHER_3D)
                .weather7dApi(HeFengWeatherConstants.HE_FENG_WEATHER_7D)
                .airNowApi(HeFengWeatherConstants.HE_FENG_AIR_NOW)
                // .appKey(HeFengWeatherConstants.HE_FENG_DEFAULT_KEY)
                .appKey(appKey)
                .build();
    }

}
