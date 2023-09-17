package cn.xeblog.plugin.game.tetris;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import lombok.Getter;

/**
 * @description: 游戏面板
 * @author: sherlock
 * @date: 2023-09-07 14:02:18
 * 原作者：我是小木鱼
 * 原文地址：https://blog.csdn.net/lag_csdn/article/details/124711977
 */
public class TetrisUI extends JPanel implements KeyListener, ActionListener {
    private static final long serialVersionUID = 1L;

    private int               width            = 330;
    private int               height           = 450;

    /** 游戏逻辑 */
    private TetrisLogic       tetrisLogic;

    // 当前速度
    @Getter
    private int               speed;

    /** 游戏地图（共21行12列，该地图包含墙和固定死的方块信息） */
    private byte[][]          map;

    /** 是否接收键盘信息（true-接收，false-不接收） */
    private boolean           isAcceptKey      = true;

    /** 定时器 */
    private Timer             timer;

    /**
     * 游戏是否结束
     */
    private boolean isOver;

    /**
     * 功能：构造函数<br>
     */
    public TetrisUI(int speed) {
        this.speed = speed;
        this.initJPanel();
        //游戏逻辑
        this.tetrisLogic = new TetrisLogic();
        //初始化游戏
        this.initGame();
        //初始化定时器
        this.initTimer(speed);
    }

    private void initTimer(int speed) {
        //获取方块下落速度
        int delay = 600 - speed * 100;
        if (speed > 5) {
            delay = 101 - (speed - 5) * 20;
        }
        //定时器
        timer = new Timer(delay, this);
        timer.start();
    }

    private void initJPanel() {
        //与主窗口大小保持一致（去掉菜单高度）
        this.setSize(width, height);
        //获得焦点（没焦点就不能截获键盘监听）
        this.setFocusable(true);
        //键盘监听
        this.addKeyListener(this);

        TetrisUI tetrisUI = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                tetrisUI.requestFocusInWindow();
            }
        });

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                tetrisUI.pauseGame();
            }
        });
    }

    /**
     * 功能：初始化游戏<br>
     */
    private void initGame() {
        //初始化游戏逻辑
        this.tetrisLogic.init();
        //得到地图
        this.map = this.tetrisLogic.getMap();
        //重画地图
        this.repaint();
    }

    /**
     * 功能：绘图<br>
     * 备注：给我地图剩下的你就不用管了。<br>
     */
    @Override
    public void paint(Graphics g) {
        int blockSize = 20; //默认方块大小
        int row = 0;
        int column = 0;

        //调用父类,让其做一些事前的工作，如刷新屏幕等
        super.paint(g);

        //画大地图中已经固定的方块和围墙
        //==================地图==================
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 7, 7, 0, 0, 0, 0, 0, 0, -1},
        //{-1, 0, 0, 7, 7, 0, 0, 1, 1, 1, 1, -1},
        //{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1, -1}
        //==================结束==================
        row = this.map.length;
        column = this.map[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (this.map[i][j] == -1) //墙
                {
                    g.setColor(Color.GRAY);
                    g.fillRect(j * blockSize, i * blockSize, blockSize, blockSize);
                    g.setColor(Color.WHITE);
                    g.drawRect(j * blockSize, i * blockSize, blockSize, blockSize);
                } else if (this.map[i][j] > 0) //方块
                {
                    switch (this.map[i][j]) {
                        case 1:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 2:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 3:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 4:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 5:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 6:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 7:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                    }
                    g.fillRect(j * blockSize, i * blockSize, blockSize, blockSize);
                    g.setColor(Color.WHITE);
                    g.drawRect(j * blockSize, i * blockSize, blockSize, blockSize);
                }
            }
        }

        //画当前方块
        byte[][] curShape = this.tetrisLogic.getShape();
        row = curShape.length;
        column = curShape[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (curShape[i][j] > 0) //方块
                {
                    switch (curShape[i][j]) {
                        case 1:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 2:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 3:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 4:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 5:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 6:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 7:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                    }
                    g.fillRect((j + this.tetrisLogic.getShapeColumn()) * blockSize,
                        (i + this.tetrisLogic.getShapeRow()) * blockSize, blockSize, blockSize);
                    g.setColor(Color.WHITE);
                    g.drawRect((j + this.tetrisLogic.getShapeColumn()) * blockSize,
                        (i + this.tetrisLogic.getShapeRow()) * blockSize, blockSize, blockSize);
                }
            }
        }

        //画记分牌
        g.setColor(Color.BLACK);
        g.setFont(new Font("宋体", Font.PLAIN, 12));
        g.drawString("当前分数： " + this.tetrisLogic.getScore(), this.map[0].length * blockSize + 20,
            30);

        byte[][] nextShape = this.tetrisLogic.getNextShape();
        row = nextShape.length;
        column = nextShape[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (nextShape[i][j] > 0) {
                    switch (nextShape[i][j]) {
                        case 1:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 2:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 3:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 4:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 5:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 6:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                        case 7:
                            g.setColor(Color.LIGHT_GRAY);
                            break;
                    }
                    g.fillRect(this.map[0].length * blockSize + 36 + j * blockSize,
                        100 + i * blockSize, blockSize, blockSize);
                    g.setColor(Color.WHITE);
                    g.drawRect(this.map[0].length * blockSize + 36 + j * blockSize,
                        100 + i * blockSize, blockSize, blockSize);
                }
            }
        }

        if (this.isOver) {
            g.setColor(new Color(238, 133, 133));
            g.setFont(new Font("宋体", Font.PLAIN, 20));
            g.drawString("游戏结束！", 100, 100);
        }
    }

    /**
     * 功能：方块移动<br>
     */
    private void move(int keyCode) {
        if (!this.isAcceptKey) {
            return;
        }

        //开始移动
        if (!this.tetrisLogic.move(keyCode)) {
            return;
        }

        //得到移动后的新地图
        this.map = this.tetrisLogic.getMap();

        //判断是否Gave Over
        if (this.tetrisLogic.gameOver()) {
            // 标记游戏已经结束
            this.isOver = true;
            //屏蔽键盘信息
            this.isAcceptKey = false;
        }

        //重绘界面
        this.repaint();
    }

    /**
     * 功能：开始新游戏<br>
     */
    public void newGame() {
        this.isOver = false;
        this.isAcceptKey = true;
        this.initGame();
        timer.start();
    }

    /**
     * 功能：暂停游戏<br>
     */
    public void pauseGame() {
        if (this.isOver) {
            return;
        }

        this.isAcceptKey = false;
        timer.stop();
    }

    /**
     * 功能：继续游戏<br>
     */
    public void continueGame() {
        if (this.isOver) {
            return;
        }

        this.isAcceptKey = true;
        timer.start();
    }

    /**
     * 功能：键盘监听<br>
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
            || keyCode == KeyEvent.VK_RIGHT) {
            this.move(keyCode);
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * 功能：事件监听<br>
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) //定时器
        {
            this.move(KeyEvent.VK_DOWN);
        }
    }

    public int getTheWidth() {
        return this.width;
    }

    public int getTheHeight() {
        return this.height;
    }
}
