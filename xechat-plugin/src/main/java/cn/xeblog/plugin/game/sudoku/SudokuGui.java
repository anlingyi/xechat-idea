package cn.xeblog.plugin.game.sudoku;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.plugin.game.sudoku.algorithm.Generator;
import cn.xeblog.plugin.game.sudoku.algorithm.Grid;
import cn.xeblog.plugin.game.sudoku.algorithm.Solver;
import cn.xeblog.plugin.game.sudoku.enums.Level;
import cn.xeblog.plugin.game.sudoku.enums.PanelSize;
import cn.xeblog.plugin.game.sudoku.enums.RealTimeTip;
import cn.xeblog.plugin.game.sudoku.enums.Theme;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.*;

/**
 * 功能描述: 数独UI页面
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/12 11:34
 */
public class SudokuGui extends JPanel implements ActionListener {
    // 主题
    private Theme theme;

    // 题面
    private int[][] puzzleInts;
    // 题解
    private int[][] solutionInts;
    // 用户解题数据
    private final Grid checkGrid = Grid.emptyGrid();
    // 输入字符串最大长度
    private final int maxLength = 1;

    // 缓存下所有格子的初始化背景色
    private final Map<String, Color> colorMap = new HashMap<>(81);

    // 数据模块
    private final JTextField[][] chessBoard = new JTextField[9][9];

    public SudokuGui(Level level, PanelSize panelSize, RealTimeTip realTimeTip, Theme theme) {
        this.theme = theme;
        initData(level);
        initJPanel(panelSize, realTimeTip);
        repaint();
    }

    private void initData(Level level) {
        // 题面
        puzzleInts = new Generator().generate(level.getBlank()).toArray();
    }

    public void doSolution(List<JButton> buttonList) {
        TimeInterval timer = DateUtil.timer();

        // 题解
        Grid grid = Grid.of(puzzleInts);
        new Solver().solve(grid);
        solutionInts = grid.toArray();

        //System.out.println("算法耗时[" + timer.interval() + "]ms");

        // 刷新按钮
        buttonList.forEach(b -> {
            b.setEnabled(true);
            b.repaint();
        });
    }

    private void initJPanel(PanelSize panelSize, RealTimeTip realTimeTip) {
        this.setPreferredSize(new Dimension(panelSize.getTableWidth(), panelSize.getTableHeight()));
        this.setLayout(new GridLayout(9, 9));
        this.setVisible(true);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // 设置JTextField的内容
                JTextField jTextField = new JTextField();
                chessBoard[i][j] = jTextField;
                this.add(jTextField);

                jTextField.setVisible(true);
                jTextField.setFont(new Font("", Font.BOLD, panelSize.getFontSize()));
                jTextField.setHorizontalAlignment(JTextField.CENTER);
                setBackgroundColor(i, j);

                // 文本填入处理
                jTextField.setDocument(generatePlainDocument());

                // 实时检测
                if (realTimeTip.isEnabled()) {
                    jTextField.addFocusListener(generateFocusListener(i, j));
                }

                if (puzzleInts[i][j] == 0) {
                    jTextField.setForeground(theme.getPuzzleForeground());
                } else {
                    jTextField.setForeground(theme.getSolutionForeground());
                    jTextField.setText(Integer.toString(puzzleInts[i][j]));
                    jTextField.setFocusable(false); // 设置是否可获得焦点
                }

                colorMap.put(i + "-" + j, jTextField.getForeground());
            }
        }
    }

    // 文本填入处理 限制只允许输入一个数字，后填的数字覆盖前面的
    private PlainDocument generatePlainDocument() {
        return new PlainDocument() {
            @Override
            public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
                if (StrUtil.isEmpty(str)) {
                    return;
                }

                int length = 0;
                char[] s = str.toCharArray();
                for (int i = 0; i < s.length; i++) {
                    // 过滤非数字
                    if ((s[i] >= '0') && (s[i] <= '9')) {
                        s[length++] = s[i];
                    }
                }

                if (length > 0) {
                    remove(0, getLength());
                    // 直接截取过滤后的 maxLength 长度的字符
                    String text = new String(s, 0, length).substring(0, maxLength);
                    super.insertString(0, text, attr);
                }
            }
        };
    }

    private void setBackgroundColor(int row, int col) {
        // 设置九宫格的背景颜色 贴近深色系统  // TODO: 2022/8/12 后面可以升级兼容浅色系统
        if ((row / 3 == 0 && col / 3 == 0) || (row / 3 == 1 && col / 3 == 1) || (row / 3 == 2 && col / 3 == 2) || (row / 3 == 0 && col / 3 == 2) || (row / 3 == 2 && col / 3 == 0)) {
            chessBoard[row][col].setBackground(theme.getBackgroundColorUnit1());
        } else {
            chessBoard[row][col].setBackground(theme.getBackgroundColorUnit2());
        }
    }

    public JButton getCommitJButton() {
        JButton commit = new JButton("提交");
        commit.addActionListener(e -> {
            refreshCheckInts();
            int errorCount = checkSelfValues(checkGrid);
            if (errorCount > 0) {
                String failureInfo = "很遗憾，你填错了" + errorCount + "个格子！";
                JOptionPane.showMessageDialog(null, failureInfo, "挑战失败！", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String successInfo = "干得漂亮！完全正确！";
                JOptionPane.showMessageDialog(null, successInfo, "挑战成功！", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        return commit;
    }

    // 用户解题数据
    private void refreshCheckInts() {
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                String text = chessBoard[row][column].getText();
                int defaultValue = -1;
                if (StrUtil.isNotEmpty(text) && Integer.parseInt(text) != 0) {
                    defaultValue = Integer.parseInt(text);
                }
                checkGrid.getCell(row, column).setValue(defaultValue);
            }
        }
    }

    public JButton getTipsJButton() {
        JButton tips = new JButton("提示");
        tips.addActionListener(e -> {
            refreshCheckInts();
            int[][] checkInts = checkGrid.toArray();

            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    // 如果chessBoard内的文本与答案不相同
                    if (checkInts[row][column] != solutionInts[row][column]) {
                        chessBoard[row][column].setForeground(theme.getTipForegroundResult());
                        chessBoard[row][column].setText(Integer.toString(solutionInts[row][column]));
                    }
                    // 刷新背景色
                    colorMap.put(row + "" + column, chessBoard[row][column].getForeground());
                }
            }
        });
        return tips;
    }

    // 数据自检  返回true就是数据有问题   这个方法略微耗时 以后可以优化
    private int checkSelfValues(Grid checkGrid) {
        int errorCount = 0;
        for (int row = 0; row < 9; row++) {
            for (int column = 0; column < 9; column++) {
                Grid.Cell cell = checkGrid.getCell(row, column);
                int checkValue = cell.getValue();

                // 如果是题面直接跳过
                if (checkValue == puzzleInts[row][column]) {
                    continue;
                }

                // 将待检查的位置数据替换为0 假装正要填下 进行自检
                cell.setValue(0);
                if (checkValue == -1 || !checkGrid.isValidValueForCell(cell, checkValue) && checkValue != solutionInts[row][column]) {
                    errorCount++;
                }
                cell.setValue(checkValue);
            }
        }
        return errorCount;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    /**
     * 判断是否合法
     *
     * @param board  数据集
     * @param row    行索引
     * @param column 列索引
     * @param val    校验待的数
     */
    public Set<String> getConflictSet(int[][] board, int row, int column, int val) {
        Set<String> conflictSet = new HashSet<>();
        // 列校验
        for (int i = 0; i < 9; i++) {
            if (board[i][column] == val) conflictSet.add(i + "-" + column);
        }
        // 行校验
        for (int j = 0; j < 9; j++) {
            if (board[row][j] == val) conflictSet.add(row + "-" + j);
        }
        // 3 * 3 单元校验
        for (int x = row / 3 * 3, i = x; i < x + 3; i++) {
            for (int y = column / 3 * 3, j = y; j < y + 3; j++) {
                if (board[i][j] == val) conflictSet.add(i + "-" + j);
            }
        }

        // 不标记当前位置
        // conflictSet.remove(row + "-" + column);
        return conflictSet;
    }

    // 添加离焦监听 失去焦点后检测当前值是否冲突
    private FocusListener generateFocusListener(int row, int column) {
        return new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                JTextField jTextField = (JTextField) e.getComponent();
                checkValue(row, column, jTextField.getText());
            }
        };
    }

    // 校验当前填的值是否有冲突，冲突就高亮处理
    private void checkValue(int row, int column, String text) {
        if ("".equals(text)) {
            return;
        }

        refreshCheckInts();
        Set<String> conflictSet = getConflictSet(checkGrid.toArray(), row, column, Integer.parseInt(text));

        // 冲突上色
        conflictSet.forEach(s -> {
            String[] xy = s.split("-");
            JTextField jTextField = chessBoard[Integer.parseInt(xy[0])][Integer.parseInt(xy[1])];
            jTextField.setForeground(theme.getTipForegroundReal());
            jTextField.repaint();
        });

        // 还原成原来的颜色
        ThreadUtil.execAsync(() -> {
            ThreadUtils.spinMoment(3000);
            conflictSet.forEach(s -> {
                String[] xy = s.split("-");
                JTextField jTextField = chessBoard[Integer.parseInt(xy[0])][Integer.parseInt(xy[1])];
                jTextField.setForeground(colorMap.get(s));
                jTextField.repaint();
            });
        });
    }
}
