package cn.xeblog.server.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.Setting;
import cn.xeblog.commons.util.ParamsUtils;
import cn.xeblog.server.config.ServerConfig;
import cn.xeblog.server.constant.ConfigConstants;

import java.nio.charset.StandardCharsets;

/**
 * 配置工具类
 *
 * @author nn200433
 * @date 2022-07-21 021 08:55:52
 */
public class ConfigUtil {

    /**
     * 读取配置
     *
     * @param args 参数
     * @return {@link ServerConfig }
     * @author nn200433
     */
    public static ServerConfig readConfig(String[] args) {
        final String configPort = ParamsUtils.getValue(args, "-p");
        final String sensitiveWordFilePath = ParamsUtils.getValue(args, "-swfile");
        final String weatherKey = ParamsUtils.getValue(args, "-weather");
        final String translationAppId = ParamsUtils.getValue(args, "-fyAppId");
        final String translationAppKey = ParamsUtils.getValue(args, "-fyAppKey");
        final String ip2regionPath = ParamsUtils.getValue(args, "-ipfile");
        final String configPath = ParamsUtils.getValue(args, "-path");
        final String token = ParamsUtils.getValue(args, "-token");
        final String enableWS = ParamsUtils.getValue(args, "-enableWS");

        final Setting configSetting = new Setting(StrUtil.blankToDefault(configPath, "config.setting"), StandardCharsets.UTF_8, Boolean.TRUE);
        final String fileConfigPort = configSetting.getByGroup(ConfigConstants.SERVER_PORT, ConfigConstants.SERVER);
        final String fileSensitiveWordFilePath = configSetting.getByGroup(ConfigConstants.SENSITIVE_WORD_FILE, ConfigConstants.SENSITIVE_WORD);
        final String fileWeatherKey = configSetting.getByGroup(ConfigConstants.WEATHER_KEY, ConfigConstants.WEATHER);
        final String fileTranslationAppId = configSetting.getByGroup(ConfigConstants.TRANSLATION_APP_ID, ConfigConstants.TRANSLATION);
        final String fileTranslationAppKey = configSetting.getByGroup(ConfigConstants.TRANSLATION_APP_KEY, ConfigConstants.TRANSLATION);
        final String fileIp2regionPath = configSetting.getByGroup(ConfigConstants.IP2REGION_PATH, ConfigConstants.IP_SEARCH);
        final String fileToken = configSetting.getByGroup(ConfigConstants.ADMIN_TOKEN, ConfigConstants.ADMIN);
        final String fileEnableWS = configSetting.getByGroup(ConfigConstants.SERVER_ENABLE_WS, ConfigConstants.SERVER);

        return ServerConfig.builder()
                .port(Convert.toInt(StrUtil.blankToDefault(configPort, fileConfigPort), 1024))
                .sensitiveWordPath(StrUtil.blankToDefault(sensitiveWordFilePath, fileSensitiveWordFilePath))
                .weatherApiKey(StrUtil.blankToDefault(weatherKey, fileWeatherKey))
                .translationAppId(StrUtil.blankToDefault(translationAppId, fileTranslationAppId))
                .translationAppKey(StrUtil.blankToDefault(translationAppKey, fileTranslationAppKey))
                .ip2RegionPath(StrUtil.blankToDefault(ip2regionPath, fileIp2regionPath))
                .token(StrUtil.blankToDefault(token, fileToken))
                .enableWS(BooleanUtil.toBoolean(StrUtil.blankToDefault(enableWS, fileEnableWS)))
                .build();
    }

}
