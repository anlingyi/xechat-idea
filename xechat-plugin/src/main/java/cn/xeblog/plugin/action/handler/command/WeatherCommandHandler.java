package cn.xeblog.plugin.action.handler.command;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.WeatherDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.WeatherType;
import cn.xeblog.commons.util.ParamsUtils;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.annotation.DoCommand;
import cn.xeblog.plugin.enums.Command;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoCommand(Command.WEATHER)
public class WeatherCommandHandler extends AbstractCommandHandler {

    @Getter
    @AllArgsConstructor
    private enum Config {
        /**
         * 查询几天
         */
        DAY("-d");

        private String key;

        public static Config getConfig(String name) {
            for (Config value : values()) {
                if (value.getKey().equals(name)) {
                    return value;
                }
            }

            return null;
        }
    }

    @Override
    public void process(String[] args) {
        int len = args.length;
        String location = null;
        if (len > 0) {
            String name = args[0];
            if (Config.getConfig(name) == null) {
                location = name;
            }
        }
        location = StrUtil.trim(location);

        if (StrUtil.isBlank(location)) {
            ConsoleAction.showSimpleMsg("地名不能为空！");
            return;
        }

        // 查询天数参数为空时，默认查询当前温度
        String day = ParamsUtils.getValue(args, Config.DAY.getKey());
        if (StrUtil.isBlank(day)) {
            day = "0";
        }

        WeatherDTO weather = new WeatherDTO(WeatherType.build(day), location);
        MessageAction.send(weather, Action.WEATHER);
        // ConsoleAction.showSimpleMsg("正在获取天气信息，请耐心等待...");
    }

}
