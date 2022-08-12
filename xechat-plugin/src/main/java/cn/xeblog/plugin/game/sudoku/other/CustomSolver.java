package cn.xeblog.plugin.game.sudoku.other;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 功能描述: 自定义解密数独算法
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/12 15:49
 */
public class CustomSolver {

    // 结果集
    public static final int[][] result = new int[9][9];
    // 统计校验过的步数
    private static final AtomicInteger step = new AtomicInteger(0);

    /**
     * 回溯填数
     *
     * @param board 数据集
     * @param r     行索引
     * @param c     列索引
     */
    public static void dfs(int[][] board, int r, int c) {
        // 所有数填完了，输出
        if (r == 9) {
            for (int i = 0; i < 9; i++) {
                System.arraycopy(board[i], 0, result[i], 0, 9);
            }
            return;
        }

        // 空白填数
        if (board[r][c] == 0) {
            for (int v = 1; v <= 9; v++) {
                if (isValid(board, r, c, v)) {
                    int stepInt = step.getAndIncrement();
                    //Console.log("当前：r={} c={} v={} step={}", r, c, v, stepInt);
                    // 做选择
                    board[r][c] = v;
                    // 决定下一个格子
                    next(board, r, c);
                    // 撤销选择
                    board[r][c] = 0;
                }
            }
        }
        // 非空白直接决定下一个格子
        else next(board, r, c);
    }

    /**
     * 递归下一个格子
     *
     * @param board 数据集
     * @param r     行索引
     * @param c     列索引
     */
    public static void next(int[][] board, int r, int c) {
        // 到边界了换行
        if (c + 1 == 9) dfs(board, r + 1, 0);
        else dfs(board, r, c + 1);
    }

    /**
     * 判断是否合法
     *
     * @param board 数据集
     * @param r     行索引
     * @param c     列索引
     * @param val   校验待的数
     */
    public static boolean isValid(int[][] board, int r, int c, int val) {
        // 列校验
        for (int i = 0; i < 9; i++) {
            if (board[i][c] == val) return false;
        }
        // 行校验
        for (int j = 0; j < 9; j++) {
            if (board[r][j] == val) return false;
        }
        // 3 * 3 格子校验
        for (int x = r / 3 * 3, i = x; i < x + 3; i++) {
            for (int y = c / 3 * 3, j = y; j < y + 3; j++) {
                if (board[i][j] == val) return false;
            }
        }
        return true;
    }

}
