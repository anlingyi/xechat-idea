package cn.xeblog.plugin.util;

import cn.hutool.core.util.ArrayUtil;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.messages.AlertMessagesManager;

import javax.swing.*;
import java.util.Collections;

/**
 * @author LYF
 * @date 2022-07-21
 */
public class AlertMessagesUtil {
    public static void showInfoDialog(String title, String message) {
        showMessageDialog(title, message, new String[]{"确定"}, Messages.getInformationIcon());
    }

    public static void showWarningDialog(String title, String message) {
        showMessageDialog(title, message, new String[]{"确定"}, Messages.getWarningIcon());
    }

    public static void showErrorDialog(String title, String message) {
        showMessageDialog(title, message, new String[]{"确定"}, Messages.getErrorIcon());
    }

    public static boolean showYesNoDialog(String title, String message) {
        return showYesNoDialog(title, message, "确定", "取消");
    }

    public static boolean showYesNoDialog(String title, String message, String yesText, String noText) {
        return AlertMessagesManager.instance().showYesNoDialog(title, message, yesText, noText, null,
                null, Messages.getQuestionIcon(), null);
    }

    public static void showMessageDialog(String title, String message, String[] options, Icon icon) {
        AlertMessagesManager.instance().showMessageDialog(null, null, message, title,
                options, 0, -1, icon, null, null);
    }
}
