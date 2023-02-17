package cn.xeblog.server.config;

/**
 * @author anlingyi
 * @date 2023/2/17 9:19 PM
 */
public class GlobalConfig {

    /**
     * 上传的文件大小最大值，单位：KB
     */
    public static final int UPLOAD_FILE_MAX_SIZE = 2 << 10;

    /**
     * 上传的文件路径
     */
    public static final String UPLOAD_FILE_PATH = System.getProperty("user.home") + "/xechat/upload";

}
