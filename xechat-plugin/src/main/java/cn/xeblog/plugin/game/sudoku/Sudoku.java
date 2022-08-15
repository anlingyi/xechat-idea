package cn.xeblog.plugin.game.sudoku;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.sudoku.other.Level;
import cn.xeblog.plugin.game.sudoku.other.PanelSize;
import cn.xeblog.plugin.game.sudoku.other.SudokuGui;
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

    @Override
    protected void init() {

        initMainPanel();
        level = Level.EASY;
        panelSize = PanelSize.MIN;

        mainPanel.setMinimumSize(new Dimension(150, 300));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 120, 300);
        mainPanel.add(menuJPanel);

        JLabel title = new JLabel("摸鱼数独");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel levelLabel = new JLabel("难度选择：");
        levelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(levelLabel);
        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> gameLevelBox = level.getComboBox(new Dimension(40, 30));
        gameLevelBox.addActionListener(l -> level = Level.getLevel(Objects.requireNonNull(gameLevelBox.getSelectedItem()).toString()));
        vBox.add(gameLevelBox);

        vBox.add(Box.createVerticalStrut(10));
        JLabel sizeLabel = new JLabel("主面板大小：");
        sizeLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(sizeLabel);
        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> gameSizeBox = panelSize.getComboBox(new Dimension(40, 30));
        gameSizeBox.addActionListener(l -> panelSize = PanelSize.getPanelSize(Objects.requireNonNull(gameSizeBox.getSelectedItem()).toString()));
        vBox.add(gameSizeBox);

        vBox.add(Box.createVerticalStrut(20));
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
        SudokuGui sudokuGui = new SudokuGui(level, panelSize);
        mainPanel.add(sudokuGui, BorderLayout.CENTER);
        JPanel bottomPanel = getBottomPanel(sudokuGui);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    // 创建按钮面板
    private JPanel getBottomPanel(SudokuGui sudokuGui) {
        JPanel commit = new JPanel();
        commit.add(sudokuGui.getCommitJButton());
        commit.add(sudokuGui.getTipsJButton());

        JPanel tips = new JPanel();
        tips.add(getStartJButton("再来一局"));
        tips.add(getMenuJButton());

        JPanel buttonPanel = new JPanel();
        if (PanelSize.MIN == panelSize) {
            buttonPanel.setLayout(new BorderLayout());
            buttonPanel.add(commit, BorderLayout.NORTH);
            buttonPanel.add(tips, BorderLayout.CENTER);
        } else {
            buttonPanel.setLayout(new FlowLayout());
            buttonPanel.add(commit);
            buttonPanel.add(tips);
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