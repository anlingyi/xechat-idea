package cn.xeblog.plugin.game.minesweeper;

import java.awt.Color;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

/**
 * @description: 扫雷游戏逻辑
 * @author: sherlock
 * @date: 2023-09-11 11:26:40
 */
public class MinesweeperLogic {
    /** 游戏面板 */
    private MinesweeperUI minesweeperUI;

    /**
     * 功能：构造函数<br>
     */
    public MinesweeperLogic(MinesweeperUI _minesweeperUI) {
        this.minesweeperUI = _minesweeperUI;
    }

    /**
     * 功能：设置地雷数与时间提示<br>
     * 参数：_number -> 要显示的数字<br>
     * 参数：_flag -> 标识（0-地雷数，1-时间）<br>
     */
    public void setNumberTip(int _number, int _flag) {
        if (_number < 0) {
            _number = 0;
        }

        //将数字转换为3位长字符串，前面不足补零
        String tip = "000" + Integer.toString(_number);
        tip = tip.substring(tip.length() - 3, tip.length());
        if (_flag == 0) //显示剩余地雷数
        {
            if (_number > this.minesweeperUI.mineNum) {
                _number = this.minesweeperUI.mineNum;
            }
            for (int i = 0; i < 3; i++) {
                this.minesweeperUI.labelMineTip[i]
                    .setIcon(this.minesweeperUI.imageIconNumberTip[Integer
                        .parseInt(tip.substring(i, i + 1))]);
            }
        } else if (_flag == 1) //显示游戏时间
        {
            if (_number > 999) {
                _number = 999;
            }
            for (int i = 0; i < 3; i++) {
                this.minesweeperUI.labelTimeTip[i]
                    .setIcon(this.minesweeperUI.imageIconNumberTip[Integer
                        .parseInt(tip.substring(i, i + 1))]);
            }
        }

    }

    /**
     * 功能：模拟雷区组件不可用效果<br>
     * 备注：只是模拟实际不好用，鼠标点击事件仍然执行。但起码表面看起来像是只读一样。<br>
     */
    public void setMineAreaDisable() {
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                if (this.minesweeperUI.mapMine[row][column].get("flag") == 0) {
                    this.minesweeperUI.buttonMine[row][column]
                        .setIcon(this.minesweeperUI.imageIconCell);
                    this.minesweeperUI.buttonMine[row][column]
                        .setPressedIcon(this.minesweeperUI.imageIconCell);
                }
            }
        }
    }

    /**
     * 功能：重置所有的信息<br>
     */
    public void resetAll() {
        //重置地雷按钮
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                this.minesweeperUI.buttonMine[row][column]
                    .setIcon(this.minesweeperUI.imageIconCell);
                this.minesweeperUI.buttonMine[row][column]
                    .setPressedIcon(this.minesweeperUI.imageIconBlank);
                this.minesweeperUI.buttonMine[row][column].setBorder(null);
            }
        }
        //重置地雷信息
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                this.minesweeperUI.mapMine[row][column].put("number", 0); //0个雷
                this.minesweeperUI.mapMine[row][column].put("flag", 0); //未打开
            }
        }
    }

    /**
     * 功能：随机生雷并记录周围地雷数<br>
     */
    public void randomMine() {
        //随机生成地雷
        for (int i = 0; i < this.minesweeperUI.mineNum;) {
            int row = (int) (Math.random() * this.minesweeperUI.gridRows);
            int column = (int) (Math.random() * this.minesweeperUI.gridColumns);
            //判断该位置是否已经有雷
            if (this.minesweeperUI.mapMine[row][column].get("number") != -1) {
                this.minesweeperUI.mapMine[row][column].put("number", -1);
                i++;
            }
        }
        //记录周围的雷数
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                if (this.minesweeperUI.mapMine[row][column].get("number") != -1) {
                    this.minesweeperUI.mapMine[row][column].put("number",
                        this.countMineAround(row, column));
                }
            }
        }
    }

    /**
     * 功能：计算周围的雷数<br>
     * 备注：九宫格（上、下、左、右、左上、左下、右上、右下）<br>
     */
    private int countMineAround(int _row, int _column) {
        int count = 0;
        for (int row = _row - 1; row <= _row + 1; row++) {
            if (row < 0 || row >= this.minesweeperUI.gridRows) {
                continue;
            } //行出边界了
            for (int column = _column - 1; column <= _column + 1; column++) {
                if (column < 0 || column >= this.minesweeperUI.gridColumns) {
                    continue;
                } //列出边界了
                if (row == _row && column == _column) {
                    continue;
                } //自身不计算在内
                if (this.minesweeperUI.mapMine[row][column].get("number") == -1) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * 功能：显示所有的雷<br>
     */
    public void showMine() {
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                if (this.minesweeperUI.mapMine[row][column].get("number") == -1) {
                    this.minesweeperUI.buttonMine[row][column]
                        .setIcon(this.minesweeperUI.imageIconMine);
                    this.minesweeperUI.buttonMine[row][column]
                        .setPressedIcon(this.minesweeperUI.imageIconMine);
                    this.minesweeperUI.mapMine[row][column].put("flag", 1);
                }
            }
        }
    }

    /**
     * 功能：打开单元格<br>
     * 备注：调用递归法<br>
     */
    public void openCell(int _row, int _column, MinesweeperUI minesweeperUI) {
        //System.out.println(_row+","+_column);

        //如果状态是已经打开或标注小红旗了就不往下判断了
        if (this.minesweeperUI.mapMine[_row][_column].get("flag") == 1
            || this.minesweeperUI.mapMine[_row][_column].get("flag") == 2) {
            return;
        }

        //设置该格子的边框为线条形（这个很重要，要不效果会很难看）
        this.minesweeperUI.buttonMine[_row][_column]
            .setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));

        if (this.minesweeperUI.mapMine[_row][_column].get("number") == -1) //踩到地雷了
        {
            this.minesweeperUI.mapMine[_row][_column].put("flag", 1);
            this.showMine(); //显示所有雷
            this.minesweeperUI.buttonMine[_row][_column].setIcon(this.minesweeperUI.imageIconBomb); //本格子的雷特殊标记
            this.minesweeperUI.buttonMine[_row][_column]
                .setPressedIcon(this.minesweeperUI.imageIconBomb);
            this.setMineAreaDisable(); //雷区禁止点击，骗人的
            this.minesweeperUI.isGameOver = true;
            this.minesweeperUI.isStart = false;
            this.minesweeperUI.timer.stop();
            return;
        } else if (this.minesweeperUI.mapMine[_row][_column].get("number") == 0) //踩到空白处
        {
            this.minesweeperUI.mapMine[_row][_column].put("flag", 1);
            this.minesweeperUI.buttonMine[_row][_column]
                .setIcon(this.minesweeperUI.imageIconNumber[0]);
            this.minesweeperUI.buttonMine[_row][_column]
                .setPressedIcon(this.minesweeperUI.imageIconNumber[0]);
            //开始判断周围四面八方的地雷数
            for (int row = _row - 1; row <= _row + 1; row++) {
                if (row < 0 || row >= this.minesweeperUI.gridRows) {
                    continue;
                }
                for (int column = _column - 1; column <= _column + 1; column++) {
                    if (column < 0 || column >= this.minesweeperUI.gridColumns) {
                        continue;
                    }
                    if (row == _row && column == _column) {
                        continue;
                    }
                    if (this.minesweeperUI.mapMine[row][column].get("number") != -1) {
                        openCell(row, column, minesweeperUI);
                    } //如果是空白需采用递归法继续向该格子的四面八方判断地雷数，直到无空白格
                }
            }
        } else //踩到数字处
        {
            this.minesweeperUI.mapMine[_row][_column].put("flag", 1);
            this.minesweeperUI.buttonMine[_row][_column].setIcon(
                this.minesweeperUI.imageIconNumber[this.minesweeperUI.mapMine[_row][_column]
                    .get("number")]);
            this.minesweeperUI.buttonMine[_row][_column].setPressedIcon(
                this.minesweeperUI.imageIconNumber[this.minesweeperUI.mapMine[_row][_column]
                    .get("number")]);
        }

        //判断游戏是否结束
        if (this.GameOver(minesweeperUI)) {
            this.setMineAreaDisable();
            this.minesweeperUI.isGameOver = true;
            this.minesweeperUI.isStart = false;
            this.minesweeperUI.timer.stop();
        }

    }

    /**
     * 功能：打开周围的单元格<br>
     * 备注：用于鼠标左右键同时点击事件<br>
     */
    public void openCellAround(int _row, int _column) {
        //仅状态是已经打开且周围地雷数大于零时才生效
        if (this.minesweeperUI.mapMine[_row][_column].get("flag") == 1
            || this.minesweeperUI.mapMine[_row][_column].get("number") > 0) {
            //如果它周围的雷都已经标记出来（插红旗），且没错误，那么它周围未打开的非雷格子都默认打开
            //先判断它周围有几个插了红旗的地雷，是否正确
            int count = 0;
            for (int row = _row - 1; row <= _row + 1; row++) {
                if (row < 0 || row >= this.minesweeperUI.gridRows) {
                    continue;
                }
                for (int column = _column - 1; column <= _column + 1; column++) {
                    if (column < 0 || column >= this.minesweeperUI.gridColumns) {
                        continue;
                    }
                    if (row == _row && column == _column) {
                        continue;
                    }
                    if (this.minesweeperUI.mapMine[row][column].get("flag") == 2) //插上小红旗了
                    {
                        if (this.minesweeperUI.mapMine[row][column].get("number") == -1) {
                            count++;
                        } else {
                            //完蛋了，标记错误，结束游戏
                            this.minesweeperUI.buttonMine[row][column]
                                .setIcon(this.minesweeperUI.imageIconWrongMine);
                            this.minesweeperUI.buttonMine[row][column]
                                .setPressedIcon(this.minesweeperUI.imageIconWrongMine);
                            this.minesweeperUI.mapMine[row][column].put("flag", 1);
                            this.showMine();
                            this.setMineAreaDisable();
                            this.minesweeperUI.isGameOver = true;
                            this.minesweeperUI.isStart = false;
                            this.minesweeperUI.timer.stop();
                            return;
                        }
                    }
                }
            }
            if (count == this.minesweeperUI.mapMine[_row][_column].get("number")) //当前格子附近的雷都已经挖出来了，那么默认它周围非雷的格子都打开。
            {
                for (int row = _row - 1; row <= _row + 1; row++) {
                    if (row < 0 || row >= this.minesweeperUI.gridRows) {
                        continue;
                    }
                    for (int column = _column - 1; column <= _column + 1; column++) {
                        if (column < 0 || column >= this.minesweeperUI.gridColumns) {
                            continue;
                        }
                        if (row == _row && column == _column) {
                            continue;
                        }
                        if (this.minesweeperUI.mapMine[row][column].get("flag") == 0
                            || this.minesweeperUI.mapMine[row][column].get("flag") == 3) {
                            this.openCell(row, column, null);
                        }
                    }
                }
            }
        }

    }

    /**
     * 功能：判断游戏是否结束<br>
     */
    public boolean GameOver(MinesweeperUI minesweeperUI) {
        //判断未被打开的方格数与雷数是否相等
        int count = 0;
        for (int row = 0; row < this.minesweeperUI.gridRows; row++) {
            for (int column = 0; column < this.minesweeperUI.gridColumns; column++) {
                if (this.minesweeperUI.mapMine[row][column].get("flag") != 1) {
                    count++;
                }
            }
        }
        if (count == this.minesweeperUI.mineNum) {
            this.minesweeperUI.isGameOver = true;
            JOptionPane.showMessageDialog(minesweeperUI, "恭喜您取得了胜利！");
            return true;
        }

        return false;
    }

}
