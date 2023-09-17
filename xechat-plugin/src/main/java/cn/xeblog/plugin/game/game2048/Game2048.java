package cn.xeblog.plugin.game.game2048;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;

import javax.swing.*;
import java.awt.*;

/**
 * @author 浓睡不消残酒
 * @date 2022/8/11 15:11 PM
 */
@DoGame(Game.GAME_2048)
public class Game2048 extends AbstractGame {

    private JPanel mainPanel;

    @Override
    protected void init() {
        initPanel();
        mainPanel.setMinimumSize(new Dimension(150, 200));
        JPanel startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("2048小游戏");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);
        vBox.add(Box.createVerticalStrut(5));
        vBox.add(getStartGameButton());
        vBox.add(getExitButton());
        mainPanel.updateUI();
    }

    @Override
    protected void start() {
        initPanel();
        int width = 240;
        int height = 300;
        mainPanel.setMinimumSize(new Dimension(width + 10, height + 10));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        GameUI gameUI = new GameUI();
        mainPanel.add(gameUI, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getBackGameButton());
        buttonPanel.add(getExitButton());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();

        gameUI.requestFocusInWindow();
    }

    private void initPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    @Override
    protected JPanel getComponent() {
        return mainPanel;
    }

    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> start());
        return button;
    }

    private JButton getBackGameButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

}
