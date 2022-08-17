package cn.xeblog.plugin.game.sudoku;

import cn.hutool.core.thread.ThreadUtil;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.sudoku.other.Level;
import cn.xeblog.plugin.game.sudoku.other.PanelSize;
import cn.xeblog.plugin.game.sudoku.other.RealTimeTip;
import cn.xeblog.plugin.game.sudoku.other.SudokuGui;
import com.google.common.collect.Lists;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * 功能描述: 数独主方法
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/12 11:34
 */
@DoGame(Game.SUDOKU)
public class Sudoku extends AbstractGame {

    private Level level;
    private PanelSize panelSize;
    private RealTimeTip realTimeTip;

    // 提示按钮
    private JButton tips;
    // 提交按钮
    private JButton commit;
    // 再来一局按钮
    private JButton another;

    @Override
    protected void init() {

        initMainPanel();
        level = Level.EASY;
        panelSize = PanelSize.MIN;
        realTimeTip = RealTimeTip.DIS_ENABLE;

        mainPanel.setMinimumSize(new Dimension(150, 300));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 100, 330);
        mainPanel.add(menuJPanel);

        JLabel title = new JLabel("摸鱼数独");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        Dimension selectDimension = new Dimension(30, 30);

        vBox.add(Box.createVerticalStrut(20));
        JLabel levelLabel = new JLabel("难度选择：");
        levelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(levelLabel);
        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> gameLevelBox = level.getComboBox(selectDimension);
        gameLevelBox.addActionListener(l -> level = Level.getLevel(Objects.requireNonNull(gameLevelBox.getSelectedItem()).toString()));
        vBox.add(gameLevelBox);

        vBox.add(Box.createVerticalStrut(10));
        JLabel sizeLabel = new JLabel("主面板大小：");
        sizeLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(sizeLabel);
        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> gameSizeBox = panelSize.getComboBox(selectDimension);
        gameSizeBox.addActionListener(l -> panelSize = PanelSize.getPanelSize(Objects.requireNonNull(gameSizeBox.getSelectedItem()).toString()));
        vBox.add(gameSizeBox);

        vBox.add(Box.createVerticalStrut(10));
        JLabel tipLabel = new JLabel("实时提示：");
        tipLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(tipLabel);
        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> tipSelectBox = realTimeTip.getComboBox(selectDimension);
        tipSelectBox.addActionListener(l -> realTimeTip = RealTimeTip.getRealTimeTip(Objects.requireNonNull(tipSelectBox.getSelectedItem()).toString()));
        vBox.add(tipSelectBox);

        vBox.add(Box.createVerticalStrut(10));
        vBox.add(getStartJButton("开始解题"));
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }


    @Override
    protected void start() {
        initMainPanel();

        mainPanel.setMinimumSize(new Dimension(panelSize.getMainWith(), panelSize.getMainHeight()));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);
        SudokuGui sudokuGui = new SudokuGui(level, panelSize, realTimeTip);
        mainPanel.add(sudokuGui, BorderLayout.CENTER);
        JPanel bottomPanel = getBottomPanel(sudokuGui);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();

        // 解密  困难以上模式解密可能会比较耗时，异步解密后开启按钮操作
        ThreadUtil.execAsync(() -> sudokuGui.doSolution(Lists.newArrayList(commit, tips, another)));
    }

    // 创建按钮面板
    private JPanel getBottomPanel(SudokuGui sudokuGui) {
        commit = sudokuGui.getCommitJButton();
        tips = sudokuGui.getTipsJButton();
        another = getStartJButton("再来一局");
        another.setEnabled(false);

        JPanel buttonPanel1 = new JPanel();
        buttonPanel1.add(commit);
        buttonPanel1.add(tips);

        JPanel buttonPanel2 = new JPanel();
        buttonPanel2.add(another);
        buttonPanel2.add(getMenuJButton());

        JPanel buttonPanel = new JPanel();
        if (PanelSize.MIN == panelSize) {
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.add(buttonPanel1, BorderLayout.NORTH);
            buttonPanel.add(buttonPanel2, BorderLayout.CENTER);
        } else {
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(buttonPanel1);
            buttonPanel.add(buttonPanel2);
        }

        return buttonPanel;
    }

    public JButton getMenuJButton() {
        JButton another = new JButton("主菜单");
        another.addActionListener(e -> init());
        return another;
    }

    public JButton getStartJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> start());
        return another;
    }

}