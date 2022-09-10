package cn.xeblog.plugin.game.sudoku.algorithm;

import java.util.Optional;
import java.util.Random;

/**
 * 来源：https://github.com/a11n/sudoku
 * <p>
 * A Solver is capable of solving a given Sudoku {@link Grid}.
 */
public class Solver {
    private static final int EMPTY = 0;

    private final int[] values;

    /**
     * Constructs a new Solver instance.
     */
    public Solver() {
        this.values = generateRandomValues();
    }

    /**
     * Solves a given {@link Grid} using backtracking.
     *
     * @param grid the {@link Grid} to solve
     * @throws IllegalStateException in case the provided {@link Grid} is invalid.
     */
    public void solve(Grid grid) {
        boolean solvable = solve(grid, grid.getFirstEmptyCell());

        if (!solvable) {
            throw new IllegalStateException("The provided grid is not solvable.");
        } else {
            // 2022年8月16日 算法可能会出现 有多个空值的情况 填补空值
            int size = grid.getSize();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    Grid.Cell cell = grid.getCell(i, j);
                    if (cell.getValue() == 0) {
                        setEmptyValue(grid, cell);
                    }
                }
            }
        }
    }

    private boolean solve(Grid grid, Optional<Grid.Cell> cell) {
        if (!cell.isPresent()) {
            return true;
        }

        for (int value : values) {
            if (grid.isValidValueForCell(cell.get(), value)) {
                cell.get().setValue(value);
                if (solve(grid, grid.getNextEmptyCellOf(cell.get()))) return true;
                cell.get().setValue(EMPTY);
            }
        }

        return false;
    }

    private int[] generateRandomValues() {
        int[] values = {EMPTY, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        Random random = new Random();
        for (int i = 0, j = random.nextInt(9), tmp = values[j]; i < values.length;
             i++, j = random.nextInt(9), tmp = values[j]) {
            if (i == j) continue;

            values[j] = values[i];
            values[i] = tmp;
        }

        return values;
    }

    /**
     * 功能描述: 处理算法未完成的空值
     *
     * @author ☆程序员鼓励师☆
     * @date 2022/8/16 10:41
     */
    public void setEmptyValue(Grid grid, Grid.Cell cell) {
        for (int value : values) {
            if (value != EMPTY && grid.isValidValueForCell(cell, value)) {
                cell.setValue(value);
            }
        }
    }

}
