package cn.xeblog.commons.util;

import cn.hutool.core.util.ArrayUtil;

/**
 * @author anlingyi
 * @date 2022/5/21 5:37 下午
 */
public class ParamsUtils {

    public static String getValue(String[] args, String key) {
        if (ArrayUtil.isEmpty(args)) {
            return null;
        }

        String value = null;
        boolean flag = false;
        for (String arg : args) {
            if (flag) {
                value = arg;
                break;
            }
            flag = arg.equalsIgnoreCase(key);
        }
        return value;
    }

    public static boolean hasKey(String[] args, String key) {
        if (ArrayUtil.isEmpty(args)) {
            return false;
        }

        for (String arg : args) {
            if (arg.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }


}
