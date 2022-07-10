package cn.xeblog.commons.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 天气类型枚举
 *
 * @author nn200433
 * @date 2022-04-24 03:22:42
 */
public enum WeatherType {

    /**
     * 实时
     */
    NOW,

    /**
     * 未来3天
     */
    WEATHER_3D,

    /**
     * 未来7天
     */
    WEATHER_7D;

    /**
     * 构建枚举
     *
     * @param day 天数
     * @return {@link WeatherType}
     */
    public static WeatherType build(String day) {
        return StrUtil.equals("3", day) ? WEATHER_3D : (StrUtil.equals("7", day) ? WEATHER_7D : NOW);
    }

}