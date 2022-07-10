package cn.nn200433.weather.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.hutool.json.JSONUtil;
import cn.nn200433.weather.entity.RespWeatherFuture;
import cn.nn200433.weather.entity.RespWeatherNow;
import cn.nn200433.weather.service.WeatherConfigService;
import cn.nn200433.weather.service.WeatherService;
import cn.xeblog.commons.constants.HeFengWeatherConstants;
import cn.xeblog.commons.entity.weather.CurrentWeather;
import cn.xeblog.commons.entity.weather.FutureWeather;
import cn.xeblog.commons.entity.weather.WeatherConfig;
import cn.xeblog.commons.enums.Lang;
import cn.xeblog.commons.enums.Unit;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 和风天气实现
 *
 * @author nn200433
 * @date 2022-04-19 01:55:55
 */
@Slf4j
public class HeFengWeatherServiceImpl implements WeatherService {

    private WeatherConfigService weatherConfigService;

    public HeFengWeatherServiceImpl() {
        this.weatherConfigService = Singleton.get(HeFengWeatherConfigServiceImpl.class.getName(), () -> new HeFengWeatherConfigServiceImpl(""));
    }

    @Override
    public CurrentWeather getWeatherNow(String location) {
        return getWeatherNow(HeFengWeatherConstants.HE_FENG_DEFAULT_KEY, location, Lang.ZH, Unit.M);
    }

    @Override
    public CurrentWeather getWeatherNow(String key, String location) {
        return getWeatherNow(key, location, Lang.ZH, Unit.M);
    }

    @Override
    public CurrentWeather getWeatherNow(String key, String location, Lang lang, Unit unit) {
        return getWeatherNow(HeFengWeatherConstants.HE_FENG_WEATHER_NOW, key, location, Lang.ZH, Unit.M);
    }

    @Override
    public CurrentWeather getWeatherNow(String apiUrl, String key, String location, Lang lang, Unit unit) {
        // 获取参数
        if (StrUtil.isBlank(location)) {
            throw new RuntimeException("接口调用失败：参数缺失");
        }
        if (StrUtil.isBlank(apiUrl) || StrUtil.isBlank(key)) {
            WeatherConfig weatherConfig = weatherConfigService.buildConfig();
            final String weatherNowApi = weatherConfig.getWeatherNowApi();
            final String appKey = weatherConfig.getAppKey();
            if (StrUtil.isBlank(weatherNowApi) || StrUtil.isBlank(appKey)) {
                throw new RuntimeException("接口调用失败：参数缺失");
            }
            apiUrl = weatherNowApi;
            key = appKey;
        }

        // 调用接口
        HttpResponse response = HttpUtil.createRequest(Method.GET, apiUrl)
                .form("key", key)
                .form("location", location)
                .form("lang", lang.getValue())
                .form("unit", unit.getValue())
                .execute();
        if (null == response || !response.isOk()) {
            throw new RuntimeException("接口调用失败");
        }

        // 解析数据
        RespWeatherNow respWeatherNow = JSONUtil.toBean(response.body(), RespWeatherNow.class);
        if (!StrUtil.equals(HeFengWeatherConstants.HE_FENG_SUCCESS_CODE, respWeatherNow.getCode())) {
            throw new RuntimeException("接口调用失败：API状态码异常");
        }
        return respWeatherNow.getNow();
    }

    @Override
    public List<FutureWeather> getWeather3d(String location) {
        return getWeather3d(HeFengWeatherConstants.HE_FENG_DEFAULT_KEY, location);
    }

    @Override
    public List<FutureWeather> getWeather3d(String key, String location) {
        return getWeather3d(key, location, Lang.ZH, Unit.M);
    }

    @Override
    public List<FutureWeather> getWeather3d(String key, String location, Lang lang, Unit unit) {
        return getWeather3d(HeFengWeatherConstants.HE_FENG_WEATHER_3D, key, location, Lang.ZH, Unit.M);
    }

    @Override
    public List<FutureWeather> getWeather3d(String apiUrl, String key, String location, Lang lang, Unit unit) {
        // 获取参数
        if (StrUtil.isBlank(location)) {
            throw new RuntimeException("接口调用失败：参数缺失");
        }
        if (StrUtil.isBlank(apiUrl) || StrUtil.isBlank(key)) {
            WeatherConfig weatherConfig = weatherConfigService.buildConfig();
            final String weather3dApi = weatherConfig.getWeather3dApi();
            final String appKey = weatherConfig.getAppKey();
            if (StrUtil.isBlank(weather3dApi) || StrUtil.isBlank(appKey)) {
                throw new RuntimeException("接口调用失败：参数缺失");
            }
            apiUrl = weather3dApi;
            key = appKey;
        }

        return getFutureWeathers(apiUrl, key, location, lang, unit);
    }

    @Override
    public List<FutureWeather> getWeather7d(String location) {
        return getWeather7d(HeFengWeatherConstants.HE_FENG_DEFAULT_KEY, location);
    }

    @Override
    public List<FutureWeather> getWeather7d(String key, String location) {
        return getWeather7d(key, location, Lang.ZH, Unit.M);
    }

    @Override
    public List<FutureWeather> getWeather7d(String key, String location, Lang lang, Unit unit) {
        return getWeather7d(HeFengWeatherConstants.HE_FENG_WEATHER_7D, key, location, Lang.ZH, Unit.M);
    }

    @Override
    public List<FutureWeather> getWeather7d(String apiUrl, String key, String location, Lang lang, Unit unit) {
        // 获取参数
        if (StrUtil.isBlank(location)) {
            throw new RuntimeException("接口调用失败：参数缺失");
        }
        if (StrUtil.isBlank(apiUrl) || StrUtil.isBlank(key)) {
            WeatherConfig weatherConfig = weatherConfigService.buildConfig();
            final String weather7dApi = weatherConfig.getWeather7dApi();
            final String appKey = weatherConfig.getAppKey();
            if (StrUtil.isBlank(weather7dApi) || StrUtil.isBlank(appKey)) {
                throw new RuntimeException("接口调用失败：参数缺失");
            }
            apiUrl = weather7dApi;
            key = appKey;
        }

        // 调用接口
        return getFutureWeathers(apiUrl, key, location, lang, unit);
    }

    /**
     * 获取未来天气
     *
     * @param apiUrl   接口地址
     * @param key      应用程序 KEY
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link List }<{@link FutureWeather }>
     * @author nn200433
     */
    private List<FutureWeather> getFutureWeathers(String apiUrl, String key, String location, Lang lang, Unit unit) {
        // 1. 调用接口
        HttpResponse response = HttpUtil.createRequest(Method.GET, apiUrl)
                .form("key", key)
                .form("location", location)
                .form("lang", lang.getValue())
                .form("unit", unit.getValue())
                .execute();
        if (null == response || !response.isOk()) {
            throw new RuntimeException("接口调用失败");
        }

        // 2. 解析数据
        RespWeatherFuture respWeather7d = JSONUtil.toBean(response.body(), RespWeatherFuture.class);
        if (!StrUtil.equals(HeFengWeatherConstants.HE_FENG_SUCCESS_CODE, respWeather7d.getCode())) {
            throw new RuntimeException("接口调用失败：API状态码异常");
        }
        return respWeather7d.getDaily().stream().map(v -> {
            DateTime fxDate = DateUtil.parse(v.getFxDate(), DatePattern.NORM_DATE_PATTERN);
            v.setWeek(CHINESE_WEEK_NAME[fxDate.dayOfWeek() - 1]);
            return v;
        }).collect(Collectors.toList());
    }

}
