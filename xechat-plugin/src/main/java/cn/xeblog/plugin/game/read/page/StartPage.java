package cn.xeblog.plugin.game.read.page;

import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.game.read.ui.ReadmeDialog;

import javax.swing.*;
import java.awt.*;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class StartPage implements IPage {
    private JPanel startPanel;

    @Override
    public void show() {
        if (startPanel == null) {
            initUI();
        }
        UIManager.showPage(startPanel, 200, 200);
    }

    @Override
    public void initUI() {
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);

        // 标题
        startPanel.add(getTitle());

        // 功能按钮
        Box vBox = Box.createVerticalBox();
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getStartButton());
        vBox.add(getSettingButton());
        vBox.add(getExitButton());
        vBox.add(getReadmeButton());
        startPanel.add(vBox);
    }

    private JLabel getTitle() {
        JLabel title = new JLabel("阅读!");
        title.setFont(new Font("", Font.BOLD, 14));
        return title;
    }

    private JButton getStartButton() {
        JButton button = new JButton("开始阅读");
        button.addActionListener(e -> UIManager.bookshelfPage.show());
        return button;
    }

    private JButton getSettingButton() {
        JButton button = new JButton("打开设置");
        button.addActionListener(e -> UIManager.settingPage.show());
        return button;
    }

    private JButton getExitButton() {
        JButton exitButton = new JButton("退出阅读");
        exitButton.addActionListener(e -> Command.GAME_OVER.exec(null));
        return exitButton;
    }

    private JButton getReadmeButton() {
        JButton exitButton = new JButton("使用说明");
        exitButton.addActionListener(e -> {
            new ReadmeDialog();
        });
        return exitButton;
    }
}
