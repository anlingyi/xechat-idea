package cn.xeblog.server.action.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.ConsoleTable;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.WeatherDTO;
import cn.xeblog.commons.entity.weather.CityInfo;
import cn.xeblog.commons.entity.weather.CurrentWeather;
import cn.xeblog.commons.entity.weather.FutureWeather;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.enums.WeatherType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.service.CityService;
import cn.xeblog.server.service.WeatherService;
import cn.xeblog.server.service.impl.HeFengCityServiceImpl;
import cn.xeblog.server.service.impl.HeFengWeatherServiceImpl;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 天气操作处理程序
 *
 * @author nn200433
 * @date 2022/07/10
 */
@DoAction(Action.WEATHER)
public class WeatherActionHandler implements ActionHandler<WeatherDTO> {

    @Override
    public void handle(ChannelHandlerContext ctx, WeatherDTO body) {
        User user = ChannelAction.getUser(ctx);

        WeatherService weatherService = Singleton.get(HeFengWeatherServiceImpl.class);
        CityService cityService = Singleton.get(HeFengCityServiceImpl.class);

        final String location = body.getLocation();
        final WeatherType type = body.getType();

        CityInfo cityInfo = cityService.getOne(location);
        List<FutureWeather> futureWeatherList = null;
        CurrentWeather weatherNow = null;

        try {
            if (WeatherType.WEATHER_3D == type) {
                // 未来3天
                futureWeatherList = weatherService.getWeather3d(cityInfo.getLocationId());
            } else if (WeatherType.WEATHER_7D == type) {
                // 未来7天
                futureWeatherList = weatherService.getWeather7d(cityInfo.getLocationId());
            } else {
                // 当前
                weatherNow = weatherService.getWeatherNow(cityInfo.getLocationId());
            }
        } catch (Exception e) {
            if (null != user) {
                user.send(ResponseBuilder.build(null, "请设置和风天气api key", MessageType.SYSTEM));
            }
            return;
        }

        ConsoleTable consoleTable = new ConsoleTable();
        consoleTable.setSBCMode(Boolean.FALSE);
        if (null != weatherNow) {
            consoleTable.addHeader("日期", "天气", "当前温度", "体感温度");
            consoleTable.addBody(DateUtil.today(), weatherNow.getText(), weatherNow.getTemp() + "℃", weatherNow.getFeelsLike() + "℃");
        }

        if (CollUtil.isNotEmpty(futureWeatherList)) {
            consoleTable.addHeader("日期", "天气", "温度");
            futureWeatherList.forEach(weather -> {
                String textWeather = weather.getTextDay() + "转" + weather.getTextNight();
                if (StrUtil.equals(weather.getTextDay(), weather.getTextNight())) {
                    textWeather =  weather.getTextDay();
                }
                consoleTable.addBody(weather.getFxDate(), textWeather,
                        weather.getTempMin() + "℃ ~ " +  weather.getTempMax() + "℃");
            });
        }

        if (null != user) {
            final String msg = StrUtil.CRLF + cityInfo.getLocationName() + " 天气预报" + StrUtil.CRLF + consoleTable;
            user.send(ResponseBuilder.build(null, msg, MessageType.SYSTEM));
        }

    }

}
