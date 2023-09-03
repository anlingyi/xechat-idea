package cn.xeblog.plugin.util;

/**
 * @author LYF
 * @date 2022-07-21
 */
public class AlertMessagesUtil {

    public static void showInfoDialog(String title, String message) {
        showMessageDialog(title, message, "确定", "");
    }

    public static void showWarningDialog(String title, String message) {
        showMessageDialog(title, message, "确定", "");
    }

    public static void showErrorDialog(String title, String message) {
        showMessageDialog(title, message, "确定", "");
    }

    public static boolean showYesNoDialog(String title, String message) {
        return showYesNoDialog(title, message, "确定", "取消");
    }

    public static boolean showYesNoDialog(String title, String message, String yesText, String noText) {
        return new MessageDialog(title, message, yesText, noText).showAndGet();
    }

    public static void showMessageDialog(String title, String message, String yesText, String noText) {
        new MessageDialog(title, message, yesText, noText).show();
    }

}
