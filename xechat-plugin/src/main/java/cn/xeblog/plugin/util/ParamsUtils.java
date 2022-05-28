package cn.xeblog.plugin.util;

/**
 * @author anlingyi
 * @date 2022/5/21 5:37 下午
 */
public class ParamsUtils {

    public static String getValue(String[] args, String key) {
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
        for (String arg : args) {
            if (arg.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }


}
