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
    public static final String SERVER = "SERVER";
    /**
     * 服务器端口
     */
    public static final String SERVER_PORT = "port";

    // endregion

    // region ============================== 敏感词 配置 ==============================

    /**
     * 分组 - 敏感词
     */
    public static final String SENSITIVE_WORD = "SENSITIVE_WORD";
    /**
     * 敏感词文件路径
     */
    public static final String SENSITIVE_WORD_FILE = "file";

    // endregion

    // region ============================== 天气 配置 ==============================

    /**
     * 分组 - 天气
     */
    public static final String WEATHER = "WEATHER";
    /**
     * 天气 app key
     */
    public static final String WEATHER_KEY = "key";

    // endregion

    // region ============================== 翻译 配置 ==============================

    /**
     * 分组 - 翻译
     */
    public static final String TRANSLATION = "TRANSLATION";
    /**
     * 翻译应用 id
     */
    public static final String TRANSLATION_APP_ID = "appId";
    /**
     * 翻译应用 key
     */
    public static final String TRANSLATION_APP_KEY = "appKey";

    // endregion

    // region ============================== ip查询 配置 ==============================

    /**
     * 分组 - IP配置
     */
    public static final String IP = "IP";

    /**
     * 离线ip查询库地址
     */
    public static final String IP2REGION_PATH = "ip2Region_path";

    // endregion

}
