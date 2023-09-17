package cn.xeblog.commons.util;

import cn.hutool.core.util.ReUtil;

/**
 * @author anlingyi
 * @date 2023/9/17 5:25 PM
 */
public class CheckUtils {

    /**
     * 校验用户名
     *
     * @param username
     * @return
     */
    public static boolean checkUsername(String username) {
        return !ReUtil.isMatch(".*[\\u200B-\\u200D\\uFEFF\\xA0\\s\\t\\r\\n].*", username);
    }

}
