package cn.xeblog.commons.util;

/**
 * @author anlingyi
 * @date 2022/6/26 7:52 下午
 */
public class ThreadUtils {

    /**
     * 当前线程自旋等待一段时间
     *
     * @param millis 等待毫秒数
     */
    public static void spinMoment(long millis) {
        long endTime = System.currentTimeMillis() + millis;
        while (endTime > System.currentTimeMillis()) {
        }
    }

}
