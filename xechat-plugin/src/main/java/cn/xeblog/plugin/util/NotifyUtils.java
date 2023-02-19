package cn.xeblog.plugin.util;

import cn.xeblog.plugin.cache.DataCache;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;

/**
 * @author anlingyi
 * @date 2022/7/1 5:17 下午
 */
public class NotifyUtils {

    public final static String GROUP_ID = "cn.xeblog.xechat.notify";

    public static void info(String title, String content) {
        info(title, content, false);
    }

    public static void warn(String title, String content) {
        warn(title, content, false);
    }

    public static void error(String title, String content) {
        error(title, content, false);
    }

    public static void info(String title, String content, boolean checked) {
        notify(title, content, NotificationType.INFORMATION, checked);
    }

    public static void warn(String title, String content, boolean checked) {
        notify(title, content, NotificationType.WARNING, checked);
    }

    public static void error(String title, String content, boolean checked) {
        notify(title, content, NotificationType.ERROR, checked);
    }

    public static void notify(String title, String content, NotificationType notificationType, boolean checked) {
        notify(new Notification(GROUP_ID, title, content, notificationType), checked);
    }

    public static void notify(Notification notification, boolean checked) {
        if (checked) {
            switch (DataCache.msgNotify) {
                case 3:
                    return;
                case 2:
                    notification.setTitle("");
                    notification.setContent("New Bug!");
                    break;
            }
        }

        Notifications.Bus.notify(notification, DataCache.project);
    }

}
