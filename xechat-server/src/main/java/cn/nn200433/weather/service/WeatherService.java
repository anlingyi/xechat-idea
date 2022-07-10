package cn.nn200433.weather.service;


import cn.xeblog.commons.entity.weather.CurrentWeather;
import cn.xeblog.commons.entity.weather.FutureWeather;
import cn.xeblog.commons.enums.Lang;
import cn.xeblog.commons.enums.Unit;

import java.util.List;

/**
 * 天气服务 接口
 *
 * @author nn200433
 * @date 2022-04-19 10:56:15
 */
public interface WeatherService {

    /**
     * 中文星期名称
     */
    public static final String[] CHINESE_WEEK_NAME = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 实时天气
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public CurrentWeather getWeatherNow(String location);

    /**
     * 实时天气
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public CurrentWeather getWeatherNow(String key, String location);

    /**
     * 实时天气
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public CurrentWeather getWeatherNow(String key, String location, Lang lang, Unit unit);

    /**
     * 实时天气
     *
     * @param apiUrl   接口地址
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public CurrentWeather getWeatherNow(String apiUrl, String key, String location, Lang lang, Unit unit);

    /**
     * 3天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather3d(String location);

    /**
     * 3天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather3d(String key, String location);

    /**
     * 3天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather3d(String key, String location, Lang lang, Unit unit);

    /**
     * 3天预报
     *
     * @param apiUrl   接口地址
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather3d(String apiUrl, String key, String location, Lang lang, Unit unit);

    /**
     * 7天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather7d(String location);

    /**
     * 7天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather7d(String key, String location);

    /**
     * 7天预报
     *
     * <p>注意：使用内置静态配置信息</p>
     *
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather7d(String key, String location, Lang lang, Unit unit);

    /**
     * 7天预报
     *
     * @param apiUrl   接口地址
     * @param key      用户认证key
     * @param location 地区ID 或 经度纬度坐标
     * @param lang     多语言设置，默认中文，当数据不匹配你设置的语言时，将返回英文或其本地语言结果。
     * @param unit     度量衡单位参数选择，例如温度选摄氏度或华氏度、公里或英里。默认公制单位
     * @return {@link CurrentWeather }
     * @author nn200433
     */
    public List<FutureWeather> getWeather7d(String apiUrl, String key, String location, Lang lang, Unit unit);

}
