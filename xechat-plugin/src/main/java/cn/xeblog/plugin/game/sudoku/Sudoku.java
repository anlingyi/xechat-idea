package cn.xeblog.plugin.game.sudoku;

import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.sudoku.other.Level;
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

    @Override
    protected void init() {

        initMainPanel();
        level = Level.EASY;

        mainPanel.setMinimumSize(new Dimension(150, 200));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(menuJPanel);

        JLabel title = new JLabel("摸鱼数独");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel modelLabel = new JLabel("难度选择：");
        modelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(modelLabel);

        vBox.add(Box.createVerticalStrut(5));
        ComboBox<String> gameLevelBox = new ComboBox<>();
        gameLevelBox.setPreferredSize(new Dimension(40, 30));
        for (Level value : Level.values()) {
            gameLevelBox.addItem(value.getMemo());
        }
        gameLevelBox.setSelectedItem(level.getMemo());

        gameLevelBox.addActionListener(l -> level = Level.getLevel(Objects.requireNonNull(gameLevelBox.getSelectedItem()).toString()));
        vBox.add(gameLevelBox);

        vBox.add(Box.createVerticalStrut(20));

        JButton startButton = new JButton("开始解题");
        startButton.addActionListener(e -> start());

        vBox.add(startButton);
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }


    @Override
    protected void start() {
        initMainPanel();

        int width = 400;
        int height = 400;

        mainPanel.setMinimumSize(new Dimension(width + 10, height + 10));
        mainPanel.setLayout(new BorderLayout());

        mainPanel.add(Box.createVerticalStrut(5), BorderLayout.NORTH);
        SudokuGui sudokuGui = new SudokuGui(level, width, height);
        mainPanel.add(sudokuGui, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(sudokuGui.getCommitJButton());
        buttonPanel.add(sudokuGui.getTipsJButton());
        buttonPanel.add(getAnotherJButton());
        buttonPanel.add(getMenuJButton());
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();

        sudokuGui.requestFocusInWindow();
    }

    public JButton getMenuJButton() {
        JButton another = new JButton("主菜单");
        another.addActionListener(e -> init());
        return another;
    }

    public JButton getAnotherJButton() {
        JButton another = new JButton("再来一局");
        another.addActionListener(e -> start());
        return another;
    }

}