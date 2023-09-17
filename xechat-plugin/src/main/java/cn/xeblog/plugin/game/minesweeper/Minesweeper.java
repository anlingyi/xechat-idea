package cn.xeblog.plugin.game.minesweeper;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

/**
 * @description:
 * @author: sherlock
 * @date: 2023-09-11 11:25:53
 */
@DoGame(Game.MINESWEEPER)
public class Minesweeper extends AbstractGame {

    private int           level;

    private MinesweeperUI minesweeperUI;

    private boolean       init;

    /** 菜单控件 */
    JMenuItem             jmi_easy, jmi_normal, jmi_hard;

    private JPanel mainPanel;

    @Override
    protected void start() {
        initPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        minesweeperUI = new MinesweeperUI(level);
        mainPanel.add(minesweeperUI, BorderLayout.CENTER);
        mainPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        mainPanel.setMinimumSize(
            new Dimension(minesweeperUI.getTheWidth() + 40, minesweeperUI.getTheHeight() + 50));
        mainPanel.updateUI();

        minesweeperUI.requestFocusInWindow();
        init = false;
    }

    @Override
    protected void init() {
        // 是否初始化
        init = true;
        level = 1;

        initPanel();

        mainPanel.setMinimumSize(new Dimension(150, 300));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 100, 330);
        mainPanel.add(menuJPanel);

        JLabel title = new JLabel("扫雷");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        Dimension selectDimension = new Dimension(30, 30);

        vBox.add(Box.createVerticalStrut(20));
        JLabel levelLabel = new JLabel("难度：");
        levelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(levelLabel);
        vBox.add(Box.createVerticalStrut(5));

        ComboBox<String> gameLevelBox = getComboBox(selectDimension);
        gameLevelBox.addActionListener(l -> level = gameLevelBox.getSelectedIndex() + 1);
        vBox.add(gameLevelBox);

        vBox.add(Box.createVerticalStrut(10));
        vBox.add(getStartJButton("开始游戏"));
        vBox.add(getExitButton());

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

    // 创建按钮面板
    private JPanel getBottomPanel() {
        JPanel jPanel = new JPanel();
        jPanel.add(getStartJButton("重置本关"));
        jPanel.add(getMenuJButton());
        return jPanel;
    }

    public JButton getMenuJButton() {
        JButton menu = new JButton("主菜单");
        menu.addActionListener(e -> init());
        return menu;
    }

    public JButton getStartJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> start());
        return another;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        comboBox.addItem("简单");
        comboBox.addItem("中等");
        comboBox.addItem("困难");
        comboBox.setSelectedItem(this.getLevelStr(level));
        return comboBox;
    }

    private String getLevelStr(int levelInt) {
        switch (levelInt) {
            case 1:
                return "简单";
            case 2:
                return "中等";
            case 3:
                return "困难";
            default:
                return "简单";
        }
    }
}
