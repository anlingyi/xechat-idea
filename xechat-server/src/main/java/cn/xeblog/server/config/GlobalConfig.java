package cn.xeblog.server.config;

import cn.xeblog.commons.enums.Permissions;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2023/2/17 9:19 PM
 */
public class GlobalConfig {

    /**
     * 上传的文件路径
     */
    public static final String UPLOAD_FILE_PATH = System.getProperty("user.home") + "/xechat/upload";

    /**
     * 上传的文件大小最大值，单位：KB
     */
    public static int UPLOAD_FILE_MAX_SIZE = 2 << 10;

    /**
     * 全局权限
     */
    public static int GLOBAL_PERMIT = Permissions.ALL.getValue();

    /**
     * 用户权限缓存
     */
    public static final Map<String, Integer> USER_PERMIT_CACHE = new ConcurrentHashMap<>(32);

}
