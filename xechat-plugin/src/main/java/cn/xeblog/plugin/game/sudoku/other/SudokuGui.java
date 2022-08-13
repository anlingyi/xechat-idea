package cn.xeblog.plugin.game.sudoku.other;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.game.sudoku.algorithm.Generator;
import cn.xeblog.plugin.game.sudoku.algorithm.Grid;
import cn.xeblog.plugin.game.sudoku.algorithm.Solver;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 功能描述: 数独UI页面
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/12 11:34
 */
public class SudokuGui extends JPanel implements ActionListener {

    // 题面
    private int[][] puzzleInts;
    // 题解
    private int[][] solutionInts;
    // 用户解题数据
    private final Grid checkGrid = Grid.emptyGrid();
    // 输入字符串最大长度
    private final int maxLength = 1;

    // 数据模块
    private final JTextField[][] chessBoard = new JTextField[9][9];

    public SudokuGui(Level level, int width, int height) {
        initData(level);
        initJPanel(width, height);
        repaint();
    }

    private void initData(Level level) {
        // 题面
        puzzleInts = new Generator().generate(level.getBlank()).toArray();

        // 题解
        Grid grid = Grid.of(puzzleInts);
        new Solver().solve(grid);
        solutionInts = grid.toArray();

        // 上面的算法可能会出现 有个空值的情况
        boolean flag = false;
        out:
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (solutionInts[i][j] == 0) {
                    flag = true;
                    break out;
                }
            }
        }

        // 如果出现空值，就换个算法去生成一套数据，这个略微比上面的算法耗时   Tips: 另外，也可以不用另一套，把上面出现的空值补上就好了
        if (flag) {
            CustomSolver.dfs(puzzleInts, 0, 0);
            solutionInts = CustomSolver.result;
        }

    }

    private void initJPanel(int width, int height) {
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new GridLayout(9, 9));
        this.setVisible(true);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                // 设置JTextField的内容
                JTextField jTextField = new JTextField();
                chessBoard[i][j] = jTextField;
                this.add(jTextField);

                jTextField.setVisible(true);
                jTextField.setFont(new Font("", Font.BOLD, 18));
                jTextField.setHorizontalAlignment(JTextField.CENTER);
                setBackgroundColor(i, j);

                // 文本填入处理
                jTextField.setDocument(generatePlainDocument());

                if (puzzleInts[i][j] == 0) {
                    jTextField.setForeground(Color.gray);
                } else {
                    jTextField.setText(Integer.toString(puzzleInts[i][j]));
                    jTextField.setFocusable(false); // 设置是否可获得焦点
                }
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
            chessBoard[row][col].setBackground(new Color(60, 63, 65));
        } else {
            chessBoard[row][col].setBackground(new Color(68, 72, 74));
        }
    }

    public JButton getCommitJButton() {
        JButton commit = new JButton("提交");
        commit.addActionListener(e -> {
            getCheckInts();
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
    private void getCheckInts() {
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
            getCheckInts();
            int[][] checkInts = checkGrid.toArray();

            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < 9; column++) {
                    // 如果chessBoard内的文本与答案不相同
                    if (checkInts[row][column] != solutionInts[row][column]) {
                        chessBoard[row][column].setForeground(new Color(253, 106, 104));
                        chessBoard[row][column].setText(Integer.toString(solutionInts[row][column]));
                    }
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

}
