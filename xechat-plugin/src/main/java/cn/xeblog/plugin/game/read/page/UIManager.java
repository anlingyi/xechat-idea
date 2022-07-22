package cn.xeblog.plugin.game.read.page;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.messages.AlertMessagesManager;

import javax.swing.*;
import java.awt.*;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class UIManager {
    /**
     * 主界面
     */
    public static JPanel mainPanel;
    public static StartPage startPage;
    public static BookshelfPage bookshelfPage;
    public static DirectoryPage directoryPage;
    public static ReadPage readPage;
    public static SettingPage settingPage;

    public static void showPage(JComponent panel, int width, int height) {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(width, height));

        mainPanel.add(panel);
        mainPanel.updateUI();
    }
}
