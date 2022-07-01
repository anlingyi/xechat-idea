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
        notify(title, content, NotificationType.INFORMATION);
    }

    public static void warn(String title, String content) {
        notify(title, content, NotificationType.WARNING);
    }

    public static void error(String title, String content) {
        notify(title, content, NotificationType.ERROR);
    }

    public static void notify(String title, String content, NotificationType notificationType) {
        notify(new Notification(GROUP_ID, title, content, notificationType));
    }

    public static void notify(Notification notification) {
        switch (DataCache.msgNotify) {
            case 3:
                return;
            case 2:
                notification.setTitle("");
                notification.setContent("New Bug!");
                break;
        }

        Notifications.Bus.notify(notification, DataCache.project);
    }

}
