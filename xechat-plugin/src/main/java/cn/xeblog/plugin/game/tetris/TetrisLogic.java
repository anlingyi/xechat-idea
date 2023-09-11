package cn.xeblog.plugin.game.tetris;

import java.awt.event.KeyEvent;

import javax.swing.*;

/**
 * @description: 游戏逻辑
 * @author: sherlock
 * @date: 2023-09-07 14:02:18
 * 原作者：我是小木鱼
 * 原文地址：https://blog.csdn.net/lag_csdn/article/details/124711977
 */
public class TetrisLogic {
    /** 大地图（共21行12列，该地图包含墙和后加入固定死的方块信息） */
    private byte[][] map;

    /** 方块类型（共7种） */
    private int      blockType     = 0;

    /** 方块状态（共4种） */
    private int      turnState     = 0;

    /** 当前方块图形（1-长条形方块，2-Z形方块，3-倒Z字形方块，4-J形方块，5-L字形，6-T字形方块，7-田字形方块） */
    private byte[][] shape;

    /** 当前方块图形（4×4矩阵）左上角所在行 */
    private int      shapeRow;

    /** 当前方块图形（4×4矩阵）左上角所在列 */
    private int      shapeColumn;

    /** 下一个方块类型 */
    private int      nextBlockType = -1;

    /** 下一个方块状态 */
    private int      nextTurnState = -1;

    /** 下一个方块图形 */
    private byte[][] nextShape;

    /** 得分 */
    private int      score         = 0;

    /**
     * 功能：构造函数<br>
     */
    public TetrisLogic() {
    }

    /**
     * 功能：初始化<br>
     */
    public void init() {
        //分数重置
        this.score = 0;

        //得到大地图
        this.map = TetrisMap.getMap();

        //随机生成新方块
        this.newBlock();

    }

    /**
     * 功能：随机生成新方块<br>
     */
    private void newBlock() {
        //如果当前没有下一方块（即游戏刚开局，还未生成当前方块和下一方块）
        if (this.nextBlockType == -1 && this.nextTurnState == -1) {
            this.blockType = (int) (Math.random() * 7); //随机生成7种形状中的一种
            this.turnState = (int) (Math.random() * 4); //随机生成4种旋转中的一种
            this.nextBlockType = (int) (Math.random() * 7);
            this.nextTurnState = (int) (Math.random() * 4);
        } else {
            this.blockType = this.nextBlockType;
            this.turnState = this.nextTurnState;
            this.nextBlockType = (int) (Math.random() * 7);
            this.nextTurnState = (int) (Math.random() * 4);
        }

        //获得当前方块，并设置初始位置（方块（4×4矩阵）最左上角方格初始行列位置即最顶端水平居中）
        this.shape = TetrisMap.getShape(this.blockType, this.turnState);
        this.shapeRow = 0;
        this.shapeColumn = 5;

        //获得下一个方块
        this.nextShape = TetrisMap.getShape(this.nextBlockType, this.nextTurnState);

    }

    /**
     * 功能：将方块信息添加到地图数组信息中<br>
     * 备注：方块信息是已经固定死了的<br>
     */
    private void add() {
        //添加当前方块位置状态
        int rowCount = this.shape.length;
        int columnCount = this.shape[0].length;
        for (int i = this.shapeRow; i < this.shapeRow + rowCount; i++) {
            for (int j = this.shapeColumn; j < this.shapeColumn + columnCount; j++) {
                if (this.shape[i - this.shapeRow][j - this.shapeColumn] > 0) {
                    this.map[i][j] = this.shape[i - this.shapeRow][j - this.shapeColumn];
                }
            }
        }

    }

    /**
     * 功能：销毁满行的部分<br>
     * 原理：一行一行对地图信息进行扫描，如果一行的每个元素值均大于0，说明满行，销毁该行，该行上面的方块依次下落一行。<br>
     */
    private void deleteLine() {
        int count = 0;
        int mapRowCount = this.map.length;
        int mapColumnCount = this.map[0].length;

        for (int i = 0; i < mapRowCount - 1; i++) //20行
        {
            for (int j = 1; j < mapColumnCount - 1; j++) //10列
            {
                if (this.map[i][j] > 0) {
                    count++;
                    if (count == (mapColumnCount - 2)) //满行了，可以销毁该行
                    {
                        for (int m = i; m > 0; m--) //从当前行往回计算
                        {
                            for (int n = 1; n < mapColumnCount - 1; n++) {
                                this.map[m][n] = this.map[m - 1][n];
                            }
                        }
                        this.score += 10;
                    }
                }
            }
            count = 0; //开始下一行计数
        }
    }

    /**
     * 功能：判断当前方块的位置是否合法<br>
     * 参数：_row -> 图形左上角所在行 <br>
     * 参数：_column -> 图形左上角所在列 <br>
     * 参数：_shape -> 移动变化的图形 <br>
     * 返回：0-不合法（可能与围墙或其他固定的方块位置重叠），1-合法<br>
     */
    private int blow(int _row, int _column, byte[][] _shape) {
        int rowCount = _shape.length;
        int columnCount = _shape[0].length;

        //方块所在的行列与墙或其他方块的行列都大于0或-1
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                if (_shape[i][j] > 0) //对该图形进行循环，找出大于零的数，即是方块
                {
                    //判断墙
                    if (this.map[_row + i][_column + j] == -1) {
                        return 0;
                    }
                    //判断固定方块
                    if (this.map[_row + i][_column + j] > 0) {
                        return 0;
                    }
                }
            }
        }

        return 1;
    }

    /**
     * 功能：方块移动<br>
     * 返回：true -> 可以移动旋转，需要刷新地图，false ->不可以，也不需要刷新地图<br>
     */
    public boolean move(int keyCode) {
        switch (keyCode) {
            //向上旋转
            case KeyEvent.VK_UP:
                //得到逆时针要变的旋转变形
                int tempTurnState = (this.turnState + 1) % this.shape.length;
                byte[][] turnShape = TetrisMap.getShape(this.blockType, tempTurnState);
                if (blow(this.shapeRow, this.shapeColumn, turnShape) == 1) {
                    this.shape = turnShape;
                    this.turnState = tempTurnState;
                } else {
                    return false;
                }
                break;
            //向下移动
            case KeyEvent.VK_DOWN:
                if (blow(this.shapeRow + 1, this.shapeColumn, this.shape) == 1) {
                    this.shapeRow++;
                } else {
                    this.add();
                    this.deleteLine();
                    this.newBlock();
                }
                break;
            //向左移动
            case KeyEvent.VK_LEFT:
                if (blow(this.shapeRow, this.shapeColumn - 1, this.shape) == 1) //可以向左移
                {
                    this.shapeColumn--;
                } else {
                    return false;
                }
                break;
            //向右移动
            case KeyEvent.VK_RIGHT:
                if (blow(this.shapeRow, this.shapeColumn + 1, this.shape) == 1) //可以向右移
                {
                    this.shapeColumn++;
                } else {
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * 功能：判断游戏是否结束<br>
     * 参数：true -> 已结束<br>
     * 参数：false -> 未结束<br>
     */
    public boolean gameOver() {
        //如果碰到围墙或固定方块，游戏结束。
        if (blow(this.shapeRow, this.shapeColumn, this.shape) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 功能：返回大地图（共21行12列，该地图包含墙和固定死的方块信息）<br>
     */
    public byte[][] getMap() {
        return map;
    }

    /**
     * 功能：返回当前方块图形<br>
     */
    public byte[][] getShape() {
        return shape;
    }

    /**
     * 功能：返回当前方块图形（4×4矩阵）左上角所在行<br>
     */
    public int getShapeRow() {
        return shapeRow;
    }

    /**
     * 功能：返回当前方块图形（4×4矩阵）左上角所在列<br>
     */
    public int getShapeColumn() {
        return shapeColumn;
    }

    /**
     * 功能：返回下一个随机方块<br>
     */
    public byte[][] getNextShape() {
        return nextShape;
    }

    /**
     * 功能：返回分数 <br>
     */
    public int getScore() {
        return score;
    }

}
