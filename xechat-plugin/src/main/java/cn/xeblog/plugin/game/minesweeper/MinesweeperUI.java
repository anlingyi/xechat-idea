package cn.xeblog.plugin.game.minesweeper;

import java.awt.*;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

/**
 * @description: 扫雷游戏面板
 * @author: sherlock
 * @date: 2023-09-11 11:26:24
 */
public class MinesweeperUI extends JPanel implements MouseListener, ActionListener {
    private static final long serialVersionUID   = -3395178511152885550L;

    /** 游戏逻辑 */
    private MinesweeperLogic  minesweeperLogic;

    /** 初级难度（9×9，10个地雷） */
    final int                 EASY               = 1;

    /** 中级难度（16×16，40个地雷） */
    final int                 NORMAL             = 2;

    /** 高级难度（30×16，99个地雷） */
    final int                 HARD               = 3;

    /** 上次游戏难度 */
    private int               oldLevel           = -1;

    /** 本次游戏难度 */
    private int               newLevel           = 0;

    /** 网格行数 */
    int                       gridRows;

    /** 网格列数 */
    int                       gridColumns;

    /** 网格尺寸 */
    final int                 gridSize           = 17;

    /** 网格宽度 */
    private int               gridsWidth;

    /** 网格高度 */
    private int               gridsHeight;

    /** 地雷数 */
    int                       mineNum;

    /** 提示区面板 */
    private JPanel            panelTip           = new JPanel();

    /** 地雷区面板 */
    private JPanel            panelMine          = new JPanel();

    /** 笑脸按钮 */
    private JButton           buttonFace         = new JButton();

    /** 地雷数提示标签 */
    JLabel                    labelMineTip[]     = new JLabel[3];

    /** 时间提示标签 */
    JLabel                    labelTimeTip[]     = new JLabel[3];

    /** 地雷按钮数组 */
    JButton[][]               buttonMine         = new JButton[16][30];

    /**
     * 地雷信息数组<br>
     * number->地雷数：-1-地雷，0到8-周围地雷数<br>
     * flag->地雷状态：0-未打开，1-已打开，2-插小旗，3-插问号<br>
     */
    @SuppressWarnings("unchecked") //数组不支持泛型
    Map<String, Integer>[][]  mapMine            = new Map[16][30];

    /** 笑脸图片 */
    private ImageIcon[]       imageIconFace      = new ImageIcon[2];

    /** 数字提示图片 */
    ImageIcon[]               imageIconNumberTip = new ImageIcon[10];

    /** 数字图片 */
    ImageIcon[]               imageIconNumber    = new ImageIcon[9];

    /** 空白图片 */
    ImageIcon                 imageIconBlank     = new ImageIcon();

    /** 方格图片 */
    ImageIcon                 imageIconCell      = new ImageIcon();

    /** 红旗图片 */
    ImageIcon                 imageIconFlag      = new ImageIcon();

    /** 问号图片 */
    ImageIcon                 imageIconQuestion  = new ImageIcon();

    /** 地雷图片 */
    ImageIcon                 imageIconMine      = new ImageIcon();

    /** 爆炸图片 */
    ImageIcon                 imageIconBomb      = new ImageIcon();

    /** 找雷错误图片 */
    ImageIcon                 imageIconWrongMine = new ImageIcon();

    /** 时间提示数字 */
    int                       timeTip            = 0;

    /** 扫雷提示数字 */
    int                       mineTip            = 0;

    /** 计时器 */
    Timer                     timer              = new Timer(1000, this);

    /** 游戏是否开始（true-开始，false-未开始） */
    boolean                   isStart            = false;

    /** 游戏是否结束（true-结束，false-未结束） */
    boolean                   isGameOver         = true;

    /**
     * 功能：构造函数<br>
     */
    public MinesweeperUI(int level) {
        //主面板初始化
        this.setBackground(Color.LIGHT_GRAY);
        this.setBorder(BorderFactory.createRaisedBevelBorder()); //边框凸起
        this.setLayout(null); //自由布局

        //提示区面板初始化
        this.panelTip.setBackground(Color.LIGHT_GRAY);
        this.panelTip.setBorder(BorderFactory.createLoweredBevelBorder()); //边框下凹
        this.panelTip.setLayout(null);
        this.add(this.panelTip);

        //地雷区面板初始化
        this.panelMine.setBackground(Color.LIGHT_GRAY);
        this.panelMine.setBorder(BorderFactory.createLoweredBevelBorder()); //边框下凹
        this.add(this.panelMine);

        //地雷数提示标签初始化
        for (int i = 0; i < this.labelMineTip.length; i++) {
            this.labelMineTip[i] = new JLabel();
        }

        //笑脸按钮初始化
        this.buttonFace.addActionListener(this); //事件监听只能添加一次，若多次添加会造成监听事件重复执行。

        //时间提示标签初始化
        for (int i = 0; i < this.labelTimeTip.length; i++) {
            this.labelTimeTip[i] = new JLabel();
        }

        //地雷按钮初始化
        for (int row = 0; row < this.buttonMine.length; row++) {
            for (int column = 0; column < this.buttonMine[0].length; column++) {
                this.buttonMine[row][column] = new JButton();
                this.buttonMine[row][column].addMouseListener(this);
                this.buttonMine[row][column].setName(row + "_" + column); //用名字来区分行列坐标
            }
        }

        //地雷信息初始化
        for (int row = 0; row < this.mapMine.length; row++) {
            for (int column = 0; column < this.mapMine[0].length; column++) {
                this.mapMine[row][column] = new HashMap<String, Integer>();
                this.mapMine[row][column].put("number", 0); //0个雷
                this.mapMine[row][column].put("flag", 0); //未打开
            }
        }

        //加载图片
        this.loadImage();

        //游戏逻辑
        this.minesweeperLogic = new MinesweeperLogic(this);

        //设置游戏难度并调整各组件大小
        this.setLevel(level);

    }

    /**
     * 功能：加载图片<br>
     * 备注：考虑Jar包问题，所以图片路径用URL格式<br>
     */
    private void loadImage() {
        try {
            //笑脸图片
            this.imageIconFace[0] = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/smile.gif"));
            this.imageIconFace[1] = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/ooo.gif"));
            //数字提示图片
            for (int i = 0; i < this.imageIconNumberTip.length; i++) {
                this.imageIconNumberTip[i] = new ImageIcon(
                    this.getClass().getResource("/games/minesweeper/c" + i + ".gif"));
            }
            //数字图片
            for (int i = 0; i < this.imageIconNumber.length; i++) {
                this.imageIconNumber[i] = new ImageIcon(
                    this.getClass().getResource("/games/minesweeper/" + i + ".gif"));
            }
            //空白图片
            this.imageIconBlank = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/blank.gif"));
            //方格图片
            this.imageIconCell = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/cell.gif"));
            //红旗图片
            this.imageIconFlag = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/flag.gif"));
            //问号图片
            this.imageIconQuestion = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/question.gif"));
            //地雷图片
            this.imageIconMine = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/mine.gif"));
            //爆炸图片
            this.imageIconBomb = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/bomb.gif"));
            //找雷错误图片
            this.imageIconWrongMine = new ImageIcon(
                this.getClass().getResource("/games/minesweeper/wrongmine.gif"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 功能：初始化游戏<br>
     */
    private void initGame() {
        //重新设置参数
        this.mineTip = this.mineNum;
        this.minesweeperLogic.setNumberTip(this.mineNum, 0);
        this.timeTip = 0;
        this.minesweeperLogic.setNumberTip(0, 1);
        this.isGameOver = false;
        this.isStart = false;
        this.timer.stop();
        //重置所有的信息
        this.minesweeperLogic.resetAll();
        //随机生雷并记录周围地雷数
        this.minesweeperLogic.randomMine();
    }

    /**
     * 功能：设置游戏难度级别<br>
     */
    public void setLevel(int _level) {
        //判断难度是否有变化
        if (this.oldLevel == _level) {
            return;
        }
        //记录难度
        this.oldLevel = _level;
        this.newLevel = _level;
        //开始调试
        if (this.newLevel == this.EASY) {
            this.gridRows = 9;
            this.gridColumns = 9;
            this.mineNum = 10;
        } else if (this.newLevel == this.NORMAL) {
            this.gridRows = 16;
            this.gridColumns = 16;
            this.mineNum = 40;
        } else if (this.newLevel == this.HARD) {
            this.gridRows = 16;
            this.gridColumns = 30;
            this.mineNum = 99;
        }
        this.gridsWidth = this.gridSize * (this.gridColumns);
        this.gridsHeight = this.gridSize * (this.gridRows);
        //重新设置主面板尺寸
        this.setBounds(0, 0, this.gridsWidth + 22, this.gridsHeight + 64);
        //重新设置提示区面板尺寸并清除上面的组件
        this.panelTip.setBounds(8, 8, this.getWidth() - 17, 36);
        this.panelTip.removeAll();
        //重新设置地雷区面板尺寸并清除上面的组件
        this.panelMine.setBounds(8, 50, this.gridsWidth + 5, this.gridsHeight + 5);
        this.panelMine.removeAll();
        //重新设置笑脸按钮
        this.buttonFace.setBounds((this.panelTip.getX() + this.panelTip.getWidth() - 34) / 2, 5, 26,
            27);
        this.buttonFace.setIcon(this.imageIconFace[0]);
        this.buttonFace.setPressedIcon(this.imageIconFace[1]);
        this.buttonFace.setBorder(null); //边框太难看，不要
        this.panelTip.add(this.buttonFace);
        //重新设置地雷数提示标签并显示总地雷数
        this.labelMineTip[0].setBounds(8, 7, 13, 23);
        this.labelMineTip[1].setBounds(21, 7, 13, 23);
        this.labelMineTip[2].setBounds(34, 7, 13, 23);
        this.minesweeperLogic.setNumberTip(this.mineNum, 0);
        this.panelTip.add(this.labelMineTip[0]);
        this.panelTip.add(this.labelMineTip[1]);
        this.panelTip.add(this.labelMineTip[2]);
        //重新设置时间提示标签并显示时间数
        this.labelTimeTip[0].setBounds(this.panelTip.getX() + this.panelTip.getWidth() - 54, 7, 13,
            23);
        this.labelTimeTip[1].setBounds(this.panelTip.getX() + this.panelTip.getWidth() - 41, 7, 13,
            23);
        this.labelTimeTip[2].setBounds(this.panelTip.getX() + this.panelTip.getWidth() - 28, 7, 13,
            23);
        this.minesweeperLogic.setNumberTip(0, 1);
        this.panelTip.add(this.labelTimeTip[0]);
        this.panelTip.add(this.labelTimeTip[1]);
        this.panelTip.add(this.labelTimeTip[2]);
        //重新设置地雷区按钮
        this.panelMine.setLayout(null); //千万不要用GridLayout布局，那就是一个坑啊
        for (int row = 0; row < this.gridRows; row++) {
            for (int column = 0; column < this.gridColumns; column++) {
                this.buttonMine[row][column].setIcon(this.imageIconCell);
                this.buttonMine[row][column].setPressedIcon(this.imageIconBlank);
                this.buttonMine[row][column].setBorder(null); //先不要边框，极其难看。但后面开始挖雷时又必须设置边框，要不更极其难看。
                this.buttonMine[row][column].setBounds(column * this.gridSize + 2,
                    row * this.gridSize + 2, this.gridSize, this.gridSize);
                this.panelMine.add(this.buttonMine[row][column]);
            }
        }
        //初始化游戏
        this.initGame();
    }

    /**
     * 功能：开始新游戏<br>
     */
    public void newGame() {
        this.initGame();
    }

    /**
     * 功能：事件监听<br>
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.timer) //定时器
        {
            this.timeTip++;
            this.minesweeperLogic.setNumberTip(this.timeTip, 1);
        } else if (e.getSource() == this.buttonFace) {
            this.newGame();
        }
    }

    /**
     * 功能：鼠标按下事件监听<br>
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //游戏结束，退出
        if (this.isGameOver) {
            return;
        }

        //游戏未开始，开始游戏，不退出
        if (this.isStart == false) {
            this.isStart = true;
            this.timer.start();
        }

        //判断是否鼠标左右键同时点击。该判断仅在鼠标按下事件中判断，我认为无论如何操作都可能会触发鼠标左键点击事件和鼠标右键点击事件。
        if (e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK + MouseEvent.BUTTON3_DOWN_MASK) {
            Object obj = e.getSource();
            if (obj instanceof JButton) {
                JButton jbMine = (JButton) obj;
                String location[] = jbMine.getName().split("_");
                int row = Integer.parseInt(location[0]);
                int column = Integer.parseInt(location[1]);
                this.minesweeperLogic.openCellAround(row, column); //鼠标左右键同时点击会打开周围的格子
            }
        }

    }

    /**
     * 功能：鼠标释放事件监听<br>
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        //游戏结束，退出
        if (this.isGameOver) {
            return;
        }

        //游戏未开始，开始游戏，不退出
        if (this.isStart == false) {
            this.isStart = true;
            this.timer.start();
        }

        Object obj = e.getSource();

        //鼠标左键点击
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (obj instanceof JButton) {
                JButton jbMine = (JButton) obj;
                String location[] = jbMine.getName().split("_");
                int row = Integer.parseInt(location[0]);
                int column = Integer.parseInt(location[1]);
                MinesweeperUI minesweeperUI = this;
                this.minesweeperLogic.openCell(row, column, minesweeperUI); //打开该格子
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) //鼠标右键点击
        {
            if (obj instanceof JButton) {
                JButton jbMine = (JButton) obj;
                String location[] = jbMine.getName().split("_");
                int row = Integer.parseInt(location[0]);
                int column = Integer.parseInt(location[1]);
                if (this.mapMine[row][column].get("flag") == 0) //处于未打开状态插红旗
                {
                    this.mapMine[row][column].put("flag", 2);
                    this.buttonMine[row][column].setIcon(this.imageIconFlag);
                    this.buttonMine[row][column].setPressedIcon(this.imageIconFlag);
                    //更改地雷提示数字
                    this.mineTip--;
                    this.minesweeperLogic.setNumberTip(this.mineTip, 0);
                } else if (this.mapMine[row][column].get("flag") == 2) //处于插红旗状态变问号
                {
                    this.mapMine[row][column].put("flag", 3);
                    this.buttonMine[row][column].setIcon(this.imageIconQuestion);
                    this.buttonMine[row][column].setPressedIcon(this.imageIconQuestion);
                    //更改地雷提示数字
                    this.mineTip++;
                    this.minesweeperLogic.setNumberTip(this.mineTip, 0);
                } else if (this.mapMine[row][column].get("flag") == 3) //处于问号状态变未打开
                {
                    this.mapMine[row][column].put("flag", 0);
                    this.buttonMine[row][column].setIcon(this.imageIconCell);
                    this.buttonMine[row][column].setPressedIcon(this.imageIconBlank);
                }
            }
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public int getTheWidth() {
        return this.gridsWidth;
    }

    public int getTheHeight() {
        return this.gridsHeight;
    }
}
