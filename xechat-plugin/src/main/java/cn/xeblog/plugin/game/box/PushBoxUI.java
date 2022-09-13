package cn.xeblog.plugin.game.box;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.game.box.util.CustomMap;
import cn.xeblog.plugin.game.box.util.ImagesUtils;
import cn.xeblog.plugin.game.box.util.MapsUtils;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;

public class PushBoxUI extends JPanel implements ActionListener {

    private int mapRow, mapColumn, width, height;
    // 地图属性
    final int WALL = 1, BOX = 2, BOX_TARGET = 3, TARGET = 4, MAN_DOWN = 5, MAN_LEFT = 6, MAN_RIGHT = 7, MAN_UP = 8, BLANK = 9, MAN_DOWN_TARGET = 10, MAN_LEFT_TARGET = 11, MAN_RIGHT_TARGET = 12, MAN_UP_TARGET = 13;

    // 每个方格大小, 默认偏移量
    private final static int UNIT_SIZE = 30, DEFAULT_OFFSET_WIDTH = 10, DEFAULT_OFFSET_HEIGHT = 50;
    // 人物坐标
    private int row, column;
    // 是否允许回退
    private boolean couldBack = true;
    // 当前关卡
    @Getter
    private int level;
    // 当前地图
    private int[][] map;
    // 操作记录 用于撤回操作
    private final Stack<CustomMap> snapshot = new Stack<>();

    public PushBoxUI(int level) {
        this.level = level;
        // 初始化当前地图数据
        initCurrentData();
        initJPanel();
    }

    public int getTheWidth() {
        return this.width;
    }

    public int getTheHeight() {
        return this.height;
    }

    private void initJPanel() {
        this.setLayout(null);
        this.setVisible(true);
        this.addKeyListener(getKeyListener());
        this.setFocusable(true);

        PushBoxUI pushBoxUI = this;
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pushBoxUI.requestFocusInWindow();
            }
        });
    }

    // 初始化地图
    public void initCurrentData() {
        // 清除缓存步骤
        this.snapshot.clear();
        this.map = MapsUtils.getLevel(level);
        // 获取游戏区域大小及显示游戏的左上角位置
        mapRow = map.length;
        mapColumn = map[0].length;
        this.width = mapColumn * UNIT_SIZE;
        this.height = mapRow * UNIT_SIZE;
        // 获取人物当前位置
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == MAN_DOWN || map[i][j] == MAN_UP || map[i][j] == MAN_LEFT || map[i][j] == MAN_RIGHT) {
                    row = i;
                    column = j;
                    break;
                }
            }
        }
    }

    private void refreshUI(Graphics g) {
        couldBack = true;
        initCurrentData();
        g.clearRect(0, 0, width + DEFAULT_OFFSET_WIDTH, height + DEFAULT_OFFSET_HEIGHT);
        updateUI();
    }

    // 刷新父容器
    private void refreshParentUI(int level) {
        JPanel parent = (JPanel) this.getParent();
        int[][] map = MapsUtils.getLevel(level);
        int width = map[0].length * UNIT_SIZE + 40;
        int height = map.length * UNIT_SIZE + 50;
        parent.setMinimumSize(new Dimension(width, height));
        parent.updateUI();
    }

    // 重新画图
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < mapRow; i++) {
            for (int j = 0; j < mapColumn; j++) {
                if (map[i][j] != 0) {
                    Image image = ImagesUtils.getImageMap().get(map[i][j]);
                    g.clearRect(DEFAULT_OFFSET_WIDTH + j * UNIT_SIZE, DEFAULT_OFFSET_HEIGHT + i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
                    g.drawImage(image, DEFAULT_OFFSET_WIDTH + j * UNIT_SIZE, DEFAULT_OFFSET_HEIGHT + i * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE, this);
                }
            }
        }

        String tips = StrUtil.format("当前第{}关，已走{}步。", level, snapshot.size());
        g.setFont(new Font(null, Font.PLAIN, UNIT_SIZE / 2));
        g.drawString(tips, 10, 30);
    }

    private void moveUp() {
        if (isWall(row - 1, column) || (isBox(row - 1, column) && isWall(row - 2, column))) {
            //changMapData(row, column, MAN_UP_TARGET, MAN_UP_TARGET, MAN_UP);
            return;
        }
        addSnapshot();
        if (isBox(row - 1, column) && isBlank(row - 2, column)) {
            changeStandData(row, column);
            changMapData(row - 1, column, BOX_TARGET, MAN_UP_TARGET, MAN_UP);
            changMapData(row - 2, column, TARGET, BOX_TARGET, BOX);
            row--;
        } else if (isBlank(row - 1, column)) {
            changeStandData(row, column);
            changMapData(row - 1, column, BLANK, MAN_UP, MAN_UP_TARGET);
            row--;
        }
    }

    private void moveDown() {
        if (isWall(row + 1, column) || (isBox(row + 1, column) && isWall(row + 2, column))) {
            //changMapData(row, column, MAN_DOWN_TARGET, MAN_DOWN_TARGET, MAN_DOWN);
            return;
        }
        addSnapshot();
        if (isBox(row + 1, column) && isBlank(row + 2, column)) {
            changeStandData(row, column);
            changMapData(row + 1, column, BOX_TARGET, MAN_DOWN_TARGET, MAN_DOWN);
            changMapData(row + 2, column, TARGET, BOX_TARGET, BOX);
            row++;
        } else if (isBlank(row + 1, column)) {
            changeStandData(row, column);
            changMapData(row + 1, column, BLANK, MAN_DOWN, MAN_DOWN_TARGET);
            row++;
        }
    }

    private void moveLeft() {
        if (isWall(row, column - 1) || (isBox(row, column - 1) && isWall(row, column - 2))) {
            //changMapData(row, column, MAN_LEFT_TARGET, MAN_LEFT_TARGET, MAN_LEFT);
            return;
        }
        addSnapshot();
        if (isBox(row, column - 1) && isBlank(row, column - 2)) {
            changeStandData(row, column);
            changMapData(row, column - 1, BOX_TARGET, MAN_LEFT_TARGET, MAN_LEFT);
            changMapData(row, column - 2, TARGET, BOX_TARGET, BOX);
            column--;
        } else if (isBlank(row, column - 1)) {
            changeStandData(row, column);
            changMapData(row, column - 1, BLANK, MAN_LEFT, MAN_LEFT_TARGET);
            column--;
        }
    }

    private void moveRight() {
        if (isWall(row, column + 1) || (isBox(row, column + 1) && isWall(row, column + 2))) {
            //changMapData(row, column, MAN_RIGHT_TARGET, MAN_RIGHT_TARGET, MAN_RIGHT);
            return;
        }
        addSnapshot();
        if (isBox(row, column + 1) && isBlank(row, column + 2)) {
            changeStandData(row, column);
            changMapData(row, column + 1, BOX_TARGET, MAN_RIGHT_TARGET, MAN_RIGHT);
            changMapData(row, column + 2, TARGET, BOX_TARGET, BOX);
            column++;
        } else if (isBlank(row, column + 1)) {
            changeStandData(row, column);
            changMapData(row, column + 1, BLANK, MAN_RIGHT, MAN_RIGHT_TARGET);
            column++;
        }
    }

    //过关后不可撤销
    public void undo() {
        if (couldBack) {
            if (snapshot.size() > 0) {
                CustomMap priorCustomMap = snapshot.pop();
                map = priorCustomMap.getMap();
                row = priorCustomMap.getManX();
                column = priorCustomMap.getManY();
                repaint();
            } else {
                JOptionPane.showMessageDialog(null, "都撤销到姥姥家了！无法再撤销！", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "本关已完成，无法撤销！", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // 上一关
    public void prevLevel() {
        if (level > 1) {
            level--;
            refreshParentUI(level);
            refreshUI(this.getGraphics());
        }
    }

    // 下一关
    public void nextLevel() {
        if (level >= MapsUtils.getTotal()) {
            couldBack = false;
            int choice = JOptionPane.showConfirmDialog(null, "恭喜通过全部通关！是否从第一关开始？", "干得漂亮！", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                level = 1;
            } else {
                return;
            }
        } else {
            level++;
        }

        refreshParentUI(level);
        refreshUI(this.getGraphics());
    }

    // 存地图快照 用于撤回操作
    private void addSnapshot() {
        snapshot.push(new CustomMap(row, column, MapsUtils.copy2dArrays(map)));
    }

    // 判断是不是箱子
    private boolean isBox(int row, int column) {
        return map[row][column] == BOX || map[row][column] == BOX_TARGET;
    }

    // 判断是否是过道或者终点
    private boolean isBlank(int row, int column) {
        return map[row][column] == BLANK || map[row][column] == TARGET;
    }

    // 判断是不是墙
    private boolean isWall(int row, int column) {
        return map[row][column] == WALL;
    }

    // 人物当前位置更改
    private void changeStandData(int row, int column) {
        int stand = map[row][column];
        stand = stand == MAN_LEFT_TARGET || stand == MAN_RIGHT_TARGET || stand == MAN_UP_TARGET || stand == MAN_DOWN_TARGET ? TARGET : BLANK;
        map[row][column] = stand;
    }

    /**
     * 改变指定位置图案值
     *
     * @param row       行
     * @param column    列
     * @param compare   比较值
     * @param correct   true对应值
     * @param inCorrect false对应值
     */
    private void changMapData(int row, int column, int compare, int correct, int inCorrect) {
        map[row][column] = map[row][column] == compare ? correct : inCorrect;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    private KeyListener getKeyListener() {
        return new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_UP:
                        moveUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        moveDown();
                        break;
                    case KeyEvent.VK_LEFT:
                        moveLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        moveRight();
                        break;
                    case KeyEvent.VK_PAGE_UP:
                        prevLevel();
                        return;
                    case KeyEvent.VK_PAGE_DOWN:
                        nextLevel();
                        return;
                    case 8: // 兼容MacOS
                    case KeyEvent.VK_DELETE:
                        undo();
                        return;
                    default:
                        break;
                }
                repaint();
                checkFinished();
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {

            }
        };
    }

    // 判断游戏是否结束
    private void checkFinished() {
        boolean finished = true;
        for (int i = 0; i < mapRow; i++) {
            for (int j = 0; j < mapColumn; j++) {
                if (map[i][j] == TARGET || map[i][j] == MAN_DOWN_TARGET || map[i][j] == MAN_UP_TARGET || map[i][j] == MAN_LEFT_TARGET || map[i][j] == MAN_RIGHT_TARGET) {
                    finished = false;
                    break;
                }
            }
        }

        if (finished) {
            couldBack = false;
            String msg = StrUtil.format("恭喜你通过第{}关！！！本关共移动{}步。\n是否要进入下一关？", level, snapshot.size());
            int choice = JOptionPane.showConfirmDialog(null, msg, "恭喜过关！", JOptionPane.YES_NO_OPTION);
            if (choice == 0) {
                couldBack = true;
                nextLevel();
            }
        }
    }

}
