package cn.xeblog.plugin.game.tetris;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import com.intellij.openapi.ui.ComboBox;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;

/**
 * @description: 俄罗斯方块
 * @author: sherlock
 * @date: 2023-09-07 14:02:18
 * 原作者：我是小木鱼
 * 原文地址：https://blog.csdn.net/lag_csdn/article/details/124711977
 */
@DoGame(Game.TETRIS)
public class Tetris extends AbstractGame {

    private int      speed;

    private boolean  init;

    private TetrisUI tetrisUI;

    private JPanel mainPanel;

    @Override
    protected void start() {
        initPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        if (!init && tetrisUI != null) {
            speed = tetrisUI.getSpeed();
        }
        tetrisUI = new TetrisUI(speed);

        mainPanel.add(tetrisUI, BorderLayout.CENTER);
        mainPanel.add(getPausePanel(), BorderLayout.SOUTH);

        mainPanel.setMinimumSize(new Dimension(tetrisUI.getTheWidth() + 40, tetrisUI.getTheHeight() + 50));
        mainPanel.updateUI();

        tetrisUI.requestFocusInWindow();
        init = false;
    }

    @Override
    protected void init() {
        //初始化UI
        if (tetrisUI != null) {
            this.tetrisUI.pauseGame();
        }
        // 是否初始化
        init = true;
        speed = 1;

        initPanel();

        mainPanel.setMinimumSize(new Dimension(150, 300));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 100, 330);

        JLabel title = new JLabel("俄罗斯方块");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        Dimension selectDimension = new Dimension(30, 30);

        vBox.add(Box.createVerticalStrut(20));
        JLabel levelLabel = new JLabel("下落速度：");
        levelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(levelLabel);
        vBox.add(Box.createVerticalStrut(5));
        //下落速度选择
        ComboBox<Integer> speedBox = getComboBox(selectDimension);
        speedBox.addActionListener(l -> speed = speedBox.getSelectedIndex() + 1);
        vBox.add(speedBox);

        vBox.add(Box.createVerticalStrut(10));
        vBox.add(getStartJButton("开始游戏"));
        vBox.add(getTipsButton());
        vBox.add(getExitButton());
        
        mainPanel.add(menuJPanel);

        mainPanel.updateUI();
    }

    @Override
    protected JPanel getComponent() {
        return mainPanel;
    }

    protected void initPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    /**
     * 暂停游戏
     * @return
     */
    private JPanel getPausePanel() {
        JPanel jPanel = new JPanel();
        jPanel.add(getPauseJButton("暂停"));
        jPanel.add(getContinuePanel());
        return jPanel;
    }

    //继续游戏
    private JPanel getContinuePanel() {
        JPanel jPanel = new JPanel();
        jPanel.add(getContinueJButton("继续"));
        jPanel.add(getRestartPanel());
        return jPanel;
    }

    /**
     * 重新开始
     * @return
     */
    private JPanel getRestartPanel() {
        JPanel jPanel = new JPanel();
        jPanel.add(getRestartJButton("重新开始"));
        jPanel.add(getMenuJButton());
        return jPanel;
    }

    /**
     * 暂停游戏
     * @param title
     * @return
     */
    public JButton getRestartJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> {
            //重新获取鼠标焦点
            this.tetrisUI.requestFocusInWindow();
            //重新加载游戏
            this.tetrisUI.newGame();
        });
        return another;
    }

    /**
     * 暂停游戏
     * @param title
     * @return
     */
    public JButton getPauseJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> this.tetrisUI.pauseGame());
        return another;
    }

    /**
     * 继续游戏
     * @param title
     * @return
     */
    public JButton getContinueJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> {
            //重新获取鼠标焦点
            this.tetrisUI.requestFocusInWindow();
            //继续游戏
            this.tetrisUI.continueGame();
        });
        return another;
    }

    /**
     * 开始游戏
     * @param title
     * @return
     */
    public JButton getStartJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> start());
        return another;
    }

    /**
     * 返回主菜单
     * @return
     */
    public JButton getMenuJButton() {
        JButton menu = new JButton("主菜单");
        menu.addActionListener(e -> init());
        return menu;
    }

    public JButton getTipsButton() {
        JButton tips = new JButton("按键提示");
        tips.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String message = "← → 方向键控制方块移动方向<br>";
                message += "↑ 旋转方块<br>";
                message += "↓ 加速方块下落<br>";
                message = StrUtil.format("<html><body>{}<body></html>", message);
                JOptionPane.showMessageDialog(null, message, "按键提示",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return tips;
    }

    public ComboBox<Integer> getComboBox(Dimension dimension) {
        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (int i = 1; i <= 10; i++) {
            comboBox.addItem(i);
        }
        comboBox.setSelectedItem(speed);
        return comboBox;
    }

}
