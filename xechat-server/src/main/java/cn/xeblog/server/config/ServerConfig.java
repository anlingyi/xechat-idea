package cn.xeblog.server.config;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author nn200433
 * @date 2022-07-21 021 08:31:14
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {

    /**
     * 端口
     */
    private Integer port;

    /**
     * 敏感词文件地址
     */
    private String sensitiveWordPath;

    /**
     * 和风天气api key
     */
    private String weatherApiKey;

    /**
     * 百度翻译app id
     */
    private String translationAppId;

    /**
     * 百度翻译app key
     */
    private String translationAppKey;

    /**
     * 离线ip库地址
     */
    private String ip2RegionPath;

    public Integer getPort() {
        return port;
    }

    public String getSensitiveWordPath() {
        return StrUtil.equals("${SW_FILE}", sensitiveWordPath) ? null : sensitiveWordPath;
    }

    public String getWeatherApiKey() {
        return StrUtil.equals("${WEATHER_KEY}", weatherApiKey) ? null : weatherApiKey;
    }

    public String getTranslationAppId() {
        return StrUtil.equals("${BD_APP_ID}", translationAppId) ? null : translationAppId;
    }

    public String getTranslationAppKey() {
        return StrUtil.equals("${BD_APP_KEY}", translationAppKey) ? null : translationAppKey;
    }

    public String getIp2RegionPath() {
        return StrUtil.equals("${IP2REGION_PATH}", ip2RegionPath) ? null : ip2RegionPath;
    }

}
