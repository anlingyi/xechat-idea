package cn.xeblog.server.constant;

/**
 * 配置常量
 *
 * @author nn200433
 * @date 2022-07-13 01:29:51
 */
public interface ConfigConstants {

    // region ============================== 服务器 配置 ==============================

    /**
     * 分组 - 服务器
     */
    String SERVER = "SERVER";
    /**
     * 服务器端口
     */
    String SERVER_PORT = "port";

    /**
     * 是否开启WS协议
     */
    String SERVER_ENABLE_WS = "enableWS";

    // endregion

    // region ============================== 敏感词 配置 ==============================

    /**
     * 分组 - 敏感词
     */
    String SENSITIVE_WORD = "SENSITIVE_WORD";
    /**
     * 敏感词文件路径
     */
    String SENSITIVE_WORD_FILE = "file";

    // endregion

    // region ============================== 天气 配置 ==============================

    /**
     * 分组 - 天气
     */
    String WEATHER = "WEATHER";
    /**
     * 天气 app key
     */
    String WEATHER_KEY = "key";

    // endregion

    // region ============================== 翻译 配置 ==============================

    /**
     * 分组 - 翻译
     */
    String TRANSLATION = "TRANSLATION";
    /**
     * 翻译应用 id
     */
    String TRANSLATION_APP_ID = "appId";
    /**
     * 翻译应用 key
     */
    String TRANSLATION_APP_KEY = "appKey";

    // endregion

    // region ============================== ip查询 配置 ==============================

    /**
     * 分组 - IP配置
     */
    String IP_SEARCH = "IP_SEARCH";

    /**
     * 离线ip查询库地址
     */
    String IP2REGION_PATH = "ip2Region_path";

    // endregion

    // region ============================== 管理员 配置 ==============================

    /**
     * 分组 - 管理员
     */
    String ADMIN = "ADMIN";

    /**
     * 管理员令牌
     */
    String ADMIN_TOKEN = "token";

    // endregion

}
