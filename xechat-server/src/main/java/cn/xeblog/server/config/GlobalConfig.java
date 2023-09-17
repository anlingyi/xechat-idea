package cn.xeblog.server.config;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
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

    /**
     * 获取用户权限
     *
     * @param user
     * @return
     */
    public static int getUserPermit(User user) {
        int permit = Permissions.ALL.getValue();

        if (user == null) {
            return permit;
        }

        String uuid = user.getUuid();
        String ip = user.getIp();

        if (StrUtil.isNotBlank(user.getUuid())) {
            Integer permitByUuid  = USER_PERMIT_CACHE.get(uuid);
            if (permitByUuid != null) {
                return permitByUuid;
            }
        }

        if (StrUtil.isNotBlank(user.getIp())) {
            Integer permitByIp = USER_PERMIT_CACHE.get(ip);
            if (permitByIp != null) {
                return permitByIp;
            }
        }

        return permit;
    }

    /**
     * 添加用户权限
     *
     * @param user
     * @param permit
     */
    public static void addUserPermit(User user, int permit) {
        String uuid = user.getUuid();
        String ip = user.getIp();

        if (StrUtil.isNotBlank(uuid)) {
            USER_PERMIT_CACHE.put(uuid, permit);
        }
        if (StrUtil.isNotBlank(ip)) {
            USER_PERMIT_CACHE.put(ip, permit);
        }
    }

}
