package cn.xeblog.plugin.game.ngsnake;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.ngsnake.model.GameMode;
import cn.xeblog.plugin.game.ngsnake.ui.SnakeGameUI;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2022/8/5 6:24 PM
 */
@DoGame(Game.NON_GLUTTONOUS_SNAKE)
public class NonGluttonousSnake extends AbstractGame {

    private GameMode gameMode;

    private JPanel mainPanel;

    @Override
    protected void init() {
        initPanel();

        mainPanel.setMinimumSize(new Dimension(150, 200));
        JPanel startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("不贪吃蛇！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel modelLabel = new JLabel("游戏模式：");
        modelLabel.setFont(new Font("", 1, 13));
        vBox.add(modelLabel);

        vBox.add(Box.createVerticalStrut(5));
        ComboBox gameModeBox = new ComboBox();
        gameModeBox.setPreferredSize(new Dimension(40, 30));
        for (GameMode value : GameMode.values()) {
            gameModeBox.addItem(value.getName());
        }
        gameMode = GameMode.NON_GLUTTONOUS;
        gameModeBox.setSelectedItem(gameMode.getName());
        gameModeBox.addActionListener(l -> {
            GameMode selectedGameMode = GameMode.getMode(gameModeBox.getSelectedItem().toString());
            if (selectedGameMode != null) {
                gameMode = selectedGameMode;
            }
        });
        vBox.add(gameModeBox);

        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getStartGameButton());
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    protected void start() {
        initPanel();

        int width = 400;
        int height = 300;
        mainPanel.setMinimumSize(new Dimension(width + 10, height + 80));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(5), BorderLayout.NORTH);

        SnakeGameUI snakeGameUI = new SnakeGameUI(width, height, gameMode);
        mainPanel.add(snakeGameUI, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        JCheckBox pierceCheckbox = new JCheckBox("穿墙", false);
        pierceCheckbox.addChangeListener(l -> snakeGameUI.setPierced(((JCheckBox) l.getSource()).isSelected()));
        topPanel.add(pierceCheckbox);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(getBackGameButton());
        buttonPanel.add(getExitButton());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();

        snakeGameUI.requestFocusInWindow();
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

    @Override
    protected JPanel getComponent() {
        return mainPanel;
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

}
