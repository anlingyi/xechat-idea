package cn.xeblog.commons.constants;

/**
 * 和风天气常量
 *
 * @author nn200433
 * @date 2022/07/10
 */
public class HeFengWeatherConstants {

    /**
     * 和风天气接口前缀
     */
    public static final String HE_FENG_HOST = "https://devapi.qweather.com/v7";

    /**
     * 实时空气质量
     */
    public static final String HE_FENG_AIR_NOW = HE_FENG_HOST + "/air/now";

    /**
     * 实时天气
     */
    public static final String HE_FENG_WEATHER_NOW = HE_FENG_HOST + "/weather/now";

    /**
     * 3天天气预报
     */
    public static final String HE_FENG_WEATHER_3D = HE_FENG_HOST + "/weather/3d";

    /**
     * 7天天气预报
     */
    public static final String HE_FENG_WEATHER_7D = HE_FENG_HOST + "/weather/7d";

    /**
     * 和风天气默认key
     */
    public static final String HE_FENG_DEFAULT_KEY = "";

    /**
     * 调用成功代码
     */
    public static final String HE_FENG_SUCCESS_CODE = "200";

}