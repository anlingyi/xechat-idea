package cn.xeblog.plugin.game.sudoku.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * This class represents a Sudoku Grid consisting of a 9x9 matrix containing nine 3x3 sub-grids of
 * {@link Cell}s.
 */
public class Grid {

    private final Cell[][] grid;

    private Grid(Cell[][] grid) {
        this.grid = grid;
    }

    /**
     * A static factory method which returns a Grid of a given two-dimensional array of integers.
     *
     * @param grid a two-dimensional int-array representation of a Grid
     * @return a Grid instance corresponding to the provided two-dimensional int-array
     */
    public static Grid of(int[][] grid) {
        verifyGrid(grid);

        Cell[][] cells = new Cell[9][9];
        List<List<Cell>> rows = new ArrayList<>();
        List<List<Cell>> columns = new ArrayList<>();
        List<List<Cell>> boxes = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            rows.add(new ArrayList<>());
            columns.add(new ArrayList<>());
            boxes.add(new ArrayList<>());
        }

        Cell lastCell = null;
        for (int row = 0; row < grid.length; row++) {
            for (int column = 0; column < grid[row].length; column++) {
                Cell cell = new Cell(grid[row][column]);
                cells[row][column] = cell;

                rows.get(row).add(cell);
                columns.get(column).add(cell);
                boxes.get((row / 3) * 3 + column / 3).add(cell);

                if (lastCell != null) {
                    lastCell.setNextCell(cell);
                }

                lastCell = cell;
            }
        }

        for (int i = 0; i < 9; i++) {
            List<Cell> row = rows.get(i);
            for (Cell cell : row) {
                List<Cell> rowNeighbors = new ArrayList<>(row);
                rowNeighbors.remove(cell);

                cell.setRowNeighbors(rowNeighbors);
            }

            List<Cell> column = columns.get(i);
            for (Cell cell : column) {
                List<Cell> columnNeighbors = new ArrayList<>(column);
                columnNeighbors.remove(cell);

                cell.setColumnNeighbors(columnNeighbors);
            }

            List<Cell> box = boxes.get(i);
            for (Cell cell : box) {
                List<Cell> boxNeighbors = new ArrayList<>(box);
                boxNeighbors.remove(cell);

                cell.setBoxNeighbors(boxNeighbors);
            }
        }

        return new Grid(cells);
    }

    /**
     * A static factory method which returns an empty Grid.
     *
     * @return an empty Grid
     */
    public static Grid emptyGrid() {
        int[][] emptyGrid = new int[9][9];
        return Grid.of(emptyGrid);
    }

    private static void verifyGrid(int[][] grid) {
        if (grid == null)
            throw new IllegalArgumentException("grid must not be null");

        if (grid.length != 9)
            throw new IllegalArgumentException("grid must have nine rows");

        for (int[] row : grid) {
            if (row.length != 9) {
                throw new IllegalArgumentException("grid must have nine columns");
            }

            for (int value : row) {
                if (value < 0 || value > 9) {
                    throw new IllegalArgumentException("grid must contain values from 0-9");
                }
            }
        }
    }

    /**
     * Returns the size of this Grid. This method is useful if you want to iterate over all {@link
     * Cell}s. <br><br> To access one cell use {@link #getCell(int, int)}. <br><br> Note: This is the
     * size of one dimension. This Grid contains size x size {@link Cell}s.
     *
     * @return the size of this Grid
     */
    public int getSize() {
        return grid.length;
    }

    /**
     * Returns the {@link Cell} at the given position within the Grid. <br><br> This Grid has 0 to
     * {@link #getSize()} rows and 0 to {@link #getSize()} columns.
     *
     * @param row    the row which contains the {@link Cell}
     * @param column the column which contains the {@link Cell}
     * @return the {@link Cell} at the given position
     */
    public Cell getCell(int row, int column) {
        return grid[row][column];
    }

    /**
     * Checks if a given value is valid for a certain {@link Cell}. <br><br> A value is valid if it
     * does not already exist in the same row, column and box.
     *
     * @param cell  the {@link Cell} to check
     * @param value the value to validate
     * @return true if the given value is valid or false otherwise
     */
    public boolean isValidValueForCell(Cell cell, int value) {
        return isValidInRow(cell, value) && isValidInColumn(cell, value) && isValidInBox(cell, value);
    }

    private boolean isValidInRow(Cell cell, int value) {
        return !getRowValuesOf(cell).contains(value);
    }

    private boolean isValidInColumn(Cell cell, int value) {
        return !getColumnValuesOf(cell).contains(value);
    }

    private boolean isValidInBox(Cell cell, int value) {
        return !getBoxValuesOf(cell).contains(value);
    }

    private Collection<Integer> getRowValuesOf(Cell cell) {
        List<Integer> rowValues = new ArrayList<>();
        for (Cell neighbor : cell.getRowNeighbors()) rowValues.add(neighbor.getValue());
        return rowValues;
    }

    private Collection<Integer> getColumnValuesOf(Cell cell) {
        List<Integer> columnValues = new ArrayList<>();
        for (Cell neighbor : cell.getColumnNeighbors()) columnValues.add(neighbor.getValue());
        return columnValues;
    }

    private Collection<Integer> getBoxValuesOf(Cell cell) {
        List<Integer> boxValues = new ArrayList<>();
        for (Cell neighbor : cell.getBoxNeighbors()) boxValues.add(neighbor.getValue());
        return boxValues;
    }

    /**
     * Returns the first empty {@link Cell} of this Grid. <br><br> Note: The result is wrapped by an
     * {@link Optional}.
     *
     * @return a non-null value containing the first empty {@link Cell} if present
     */
    public Optional<Cell> getFirstEmptyCell() {
        Cell firstCell = grid[0][0];
        if (firstCell.isEmpty()) {
            return Optional.of(firstCell);
        }

        return getNextEmptyCellOf(firstCell);
    }

    /**
     * Returns the next empty {@link Cell} consecutively to the given {@link Cell} in this Grid.
     * <br><br> Note: The result is wrapped by an {@link Optional}.
     *
     * @param cell the {@link Cell} of which the next empty {@link Cell} should be obtained
     * @return a non-null value containing the next empty {@link Cell} if present
     */
    public Optional<Cell> getNextEmptyCellOf(Cell cell) {
        Cell nextEmptyCell = null;

        while ((cell = cell.getNextCell()) != null) {
            if (!cell.isEmpty()) {
                continue;
            }

            nextEmptyCell = cell;
            break;
        }

        return Optional.ofNullable(nextEmptyCell);
    }

    /**
     * Returns a {@link String} representation of this Grid.
     *
     * @return a {@link String} representation of this Grid.
     */
    @Override
    public String toString() {
        return StringConverter.toString(this);
    }

    /**
     * 功能描述: Grid转成二维数组
     *
     * @author ☆程序员鼓励师☆
     * @date 2022/8/12 13:21
     */
    public int[][] toArray() {
        int size = this.getSize();
        int[][] result = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                result[row][column] = this.getCell(row, column).getValue();
            }
        }
        return result;
    }

    /**
     * This class represents a Cell within a Sudoku {@link Grid}. <br><br> It features a couple of
     * convenient methods.
     */
    public static class Cell {
        private int value;
        private Collection<Cell> rowNeighbors;
        private Collection<Cell> columnNeighbors;
        private Collection<Cell> boxNeighbors;
        private Cell nextCell;

        public Cell(int value) {
            this.value = value;
        }

        /**
         * Returns the value of the Cell. <br><br> The value is a digit (1, ..., 9) or 0 if the Cell is
         * empty.
         *
         * @return the value of the Cell.
         */
        public int getValue() {
            return value;
        }

        /**
         * Indicates whether the Cell is empty or not.
         *
         * @return true if the Cell is empty, false otherwise
         */
        public boolean isEmpty() {
            return value == 0;
        }

        /**
         * Allows to change the value of the Cell.
         *
         * @param value the new value of the Cell
         */
        public void setValue(int value) {
            this.value = value;
        }

        /**
         * Returns a {@link Collection} of all other Cells in the same row than this Cell.
         *
         * @return a {@link Collection} of row neighbors
         */
        public Collection<Cell> getRowNeighbors() {
            return rowNeighbors;
        }

        /**
         * Allows to set a {@link Collection} of Cells, which are interpreted to be in the same row.
         *
         * @param rowNeighbors a {@link Collection} of row neighbors
         */
        public void setRowNeighbors(Collection<Cell> rowNeighbors) {
            this.rowNeighbors = rowNeighbors;
        }

        /**
         * Returns a {@link Collection} of all other Cells in the same column than this Cell.
         *
         * @return a {@link Collection} of column neighbors
         */
        public Collection<Cell> getColumnNeighbors() {
            return columnNeighbors;
        }

        /**
         * Allows to set a {@link Collection} of Cells, which are interpreted to be in the same column.
         *
         * @param columnNeighbors a {@link Collection} of column neighbors
         */
        public void setColumnNeighbors(Collection<Cell> columnNeighbors) {
            this.columnNeighbors = columnNeighbors;
        }

        /**
         * Returns a {@link Collection} of all other Cells in the same box than this Cell.
         *
         * @return a {@link Collection} of box neighbors
         */
        public Collection<Cell> getBoxNeighbors() {
            return boxNeighbors;
        }

        /**
         * Allows to set a {@link Collection} of Cells, which are interpreted to be in the same box.
         *
         * @param boxNeighbors a {@link Collection} of box neighbors
         */
        public void setBoxNeighbors(Collection<Cell> boxNeighbors) {
            this.boxNeighbors = boxNeighbors;
        }

        /**
         * Returns the next Cell consecutive to this Cell. <br><br> This function returns the Cell to
         * the right of each Cell if the Cell is not the last Cell in a row. It returns the first Cell
         * of the next row of each Cell if the Cell is the last Cell in a row. For the very last Cell in
         * the very last row this function returns null.
         *
         * @return the next Cell consecutive to this Cell or null if it is the last Cell.
         */
        public Cell getNextCell() {
            return nextCell;
        }

        /**
         * Allows to set a Cell which is interpreted to be the next Cell consecutive to this Cell.
         *
         * @param nextCell the next Cell consecutive to this Cell.
         */
        public void setNextCell(Cell nextCell) {
            this.nextCell = nextCell;
        }
    }

    private static class StringConverter {
        public static String toString(Grid grid) {
            StringBuilder builder = new StringBuilder();
            int size = grid.getSize();

            printTopBorder(builder);
            for (int row = 0; row < size; row++) {
                printRowBorder(builder);
                for (int column = 0; column < size; column++) {
                    printValue(builder, grid, row, column);
                    printRightColumnBorder(builder, column + 1, size);
                }
                printRowBorder(builder);
                builder.append("\n");
                printBottomRowBorder(builder, row + 1, size);
            }
            printBottomBorder(builder);

            return builder.toString();
        }

        private static void printTopBorder(StringBuilder builder) {
            builder.append("╔═══╤═══╤═══╦═══╤═══╤═══╦═══╤═══╤═══╗\n");
        }

        private static void printRowBorder(StringBuilder builder) {
            builder.append("║");
        }

        private static void printValue(StringBuilder builder, Grid grid, int row, int column) {
            int value = grid.getCell(row, column).getValue();
            String output = value != 0 ? String.valueOf(value) : " ";
            builder.append(" " + output + " ");
        }

        private static void printRightColumnBorder(StringBuilder builder, int column, int size) {
            if (column == size) {
                return;
            }

            if (column % 3 == 0) {
                builder.append("║");
            } else {
                builder.append("│");
            }
        }

        private static void printBottomRowBorder(StringBuilder builder, int row, int size) {
            if (row == size) {
                return;
            }

            if (row % 3 == 0) {
                builder.append("╠═══╪═══╪═══╬═══╪═══╪═══╬═══╪═══╪═══╣\n");
            } else {
                builder.append("╟───┼───┼───╫───┼───┼───╫───┼───┼───╢\n");
            }
        }

        private static void printBottomBorder(StringBuilder builder) {
            builder.append("╚═══╧═══╧═══╩═══╧═══╧═══╩═══╧═══╧═══╝\n");
        }
    }
}
