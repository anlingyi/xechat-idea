package cn.xeblog.plugin.game;

import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.GobangDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 五子棋
 *
 * @author anlingyi
 * @date 2020/6/5
 */
public class Gobang extends AbstractGame<GobangDTO> {

    public static JLabel tips;

    // 每个格子的边框大小
    public static final int BORDER = 10;
    // 行数
    public static final int ROWS = 15;
    // 列数
    public static final int COLS = 15;
    // 棋子大小，约为格子的3/4
    private static final int CHESS_SIZE = Math.round(BORDER * 0.75f);
    // 棋盘宽度
    private static final int WIDTH = ROWS * BORDER + BORDER;
    // 棋盘高度
    private static final int HEIGHT = ROWS * BORDER + BORDER;
    // 棋子总数
    private static final int CHESS_TOTAL = ROWS * COLS;

    // 已下棋子数据
    private int[][] chessData = new int[ROWS][COLS];
    // 当前已下棋子数
    private int currentChessTotal;
    // 棋子类型，1黑棋 2白棋
    private int type;
    // 游戏是否结束
    private boolean isGameOver;

    private int status;

    private boolean put;

    @Override
    public void handle(Response<GobangDTO> response) {
        GobangDTO gobangDTO = response.getBody();
        User user = response.getUser();

        if (putChess(gobangDTO.getX(), gobangDTO.getY(), gobangDTO.getType(), false)) {
            showTips(DataCache.username + "(你)：思考中...");
        }

        checkStatus(gobangDTO.getStatus(), user.getUsername());
    }

    private void checkStatus(int status, String username) {
        boolean flag = true;
        switch (status) {
            case 1:
                showTips("游戏结束：" + username + "这个菜鸡赢了！");
                break;
            case 2:
                showTips("游戏结束：平局~ 卧槽？？？");
                break;
            case 0:
                flag = false;
                break;
            default:
                break;
        }
        isGameOver = flag;
    }

    @Override
    protected void init() {
        super.init();
        // 设置棋盘宽高
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // 设置棋盘背景颜色
        setBackground(Color.LIGHT_GRAY);

        tips = new JLabel("", JLabel.CENTER);
        tips.setPreferredSize(new Dimension(WIDTH + 60, 30));
        tips.setFont(new Font("微软雅黑", Font.BOLD, 11));
        tips.setForeground(new Color(237, 81, 38));

        mainPanel.setPreferredSize(new Dimension(WIDTH + 100, HEIGHT + 100));
        mainPanel.add(tips);
        mainPanel.add(this);

        this.addMouseListener(new MouseAdapter() {
            // 监听鼠标点击事件
            @Override
            public void mouseClicked(MouseEvent e) {
                if (put || isGameOver) {
                    return;
                }

                if (putChess(e.getX(), e.getY(), type, true)) {
                    send(e.getX(), e.getY());
                }
            }
        });
    }

    @Override
    public void start() {
        super.start();
        if (GameAction.isProactive()) {
            type = 2;
            put = true;
        } else {
            type = 1;
        }

        String name = type == 1 ? DataCache.username : GameAction.getOpponent();
        showTips(name + "先下手为强！");
    }

    private int currentRow;
    private int currentCol;

    public boolean putChess(int x, int y, int type, boolean check) {
        if (isGameOver) {
            return false;
        }

        // 计算出对应的行列 向上取整
        int row = Math.round((float)(x - BORDER) / BORDER);
        int col = Math.round((float)(y - BORDER) / BORDER);

        if (row < 0 || col < 0 || row > ROWS - 1 || col > COLS - 1) {
            return false;
        }

        // 棋子圆心坐标
        int circleX = row * BORDER + BORDER;
        int circleY = col * BORDER + BORDER;

        // 判断鼠标点击的坐标是否在棋子圆外
        boolean notInCircle = Math.pow(circleX - x, 2) + Math.pow(circleY - y, 2) > Math.pow(CHESS_SIZE / 2, 2);

        if (notInCircle) {
            // 不在棋子圆内
            return false;
        }

        if (chessData[row][col] != 0) {
            // 此处已有棋子
            return false;
        }

        currentRow = row;
        currentCol = col;
        currentChessTotal++;
        chessData[row][col] = type;
        // 重绘
        repaint();

        if (check) {
            put = true;
            // 检查是否5连
            checkWinner(row, col, type);
            return true;
        }

        put = false;

        return true;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 画横线
        for (int i = 0; i < ROWS; i++) {
            g2.drawLine(BORDER, i * BORDER + BORDER, WIDTH - BORDER, i * BORDER + BORDER);
        }

        // 画纵线
        for (int i = 0; i < COLS; i++) {
            g2.drawLine(i * BORDER + BORDER, BORDER, i * BORDER + BORDER, HEIGHT - BORDER);
        }

        if (currentChessTotal == 0) {
            return;
        }

        // 画棋子
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int k = chessData[i][j];
                if (k == 0) {
                    continue;
                }

                if (k == 1) {
                    g2.setColor(Color.BLACK);
                } else if (k == 2) {
                    g2.setColor(Color.WHITE);
                }

                // 计算棋子外矩形左上顶点坐标
                int halfBorder = CHESS_SIZE / 2;
                int chessX = i * BORDER + BORDER - halfBorder;
                int chessY = j * BORDER + BORDER - halfBorder;

                g2.fillOval(chessX, chessY, CHESS_SIZE, CHESS_SIZE);

                if (i == currentRow && j == currentCol) {
                    // 当前棋子高亮
                    g2.setColor(Color.RED);
                    g2.drawOval(chessX,chessY, CHESS_SIZE, CHESS_SIZE);
                }
            }
        }
    }

    /**
     * 检查是否和棋
     */
    public void checkPeace() {
        if (currentChessTotal == CHESS_TOTAL) {
            peacemaker();
        }
    }

    /**
     * 检查是否5连
     *
     * @param x
     * @param y
     * @param type
     */
    public void checkWinner(int x, int y, int type) {
        // 横轴
        int k = 1;
        for (int i = 1; i < 5; i++) {
            int preX = x - i;
            if (preX < 0) {
                break;
            }

            if (chessData[preX][y] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }
        for (int i = 1; i < 5; i++) {
            int nextX = x + i;
            if (nextX > ROWS - 1) {
                break;
            }

            if (chessData[nextX][y] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 纵轴
        k = 1;
        for (int i = 1; i < 5; i++) {
            int preY = y - i;
            if (preY < 0) {
                break;
            }

            if (chessData[x][preY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }
        for (int i = 1; i < 5; i++) {
            int nextY = y + i;
            if (nextY > COLS - 1) {
                break;
            }

            if (chessData[x][nextY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 左对角线
        k = 1;
        for (int i = 1; i < 5; i++) {
            int preX = x - i;
            int preY = y - i;
            if (preX < 0 || preY < 0) {
                break;
            }

            if (chessData[preX][preY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }
        for (int i = 1; i < 5; i++) {
            int nextX = x + i;
            int nextY = y + i;
            if (nextX > ROWS - 1 || nextY > COLS - 1) {
                break;
            }

            if (chessData[nextX][nextY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 右对角线
        k = 1;
        for (int i = 1; i < 5; i++) {
            int nextX = x + i;
            int preY = y - i;
            if (nextX > ROWS - 1 || preY < 0) {
                break;
            }

            if (chessData[nextX][preY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }
        for (int i = 1; i < 5; i++) {
            int preX = x - i;
            int nextY = y + i;
            if (preX < 0 || nextY > COLS - 1) {
                break;
            }

            if (chessData[preX][nextY] != type) {
                break;
            }

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 检查是否和棋
        checkPeace();
    }

    private void gameOver() {
        checkStatus(status, DataCache.username);
    }

    private void winner() {
        status = 1;
        gameOver();
    }

    private void peacemaker() {
        status = 2;
        gameOver();
    }

    private void send(int x, int y) {
        String opponent = GameAction.getOpponent();
        GobangDTO dto = new GobangDTO();
        dto.setStatus(status);
        dto.setX(x);
        dto.setY(y);
        dto.setType(type);
        dto.setOpponentId(DataCache.userMap.get(opponent));
        MessageAction.send(dto, Action.GAME);

        showTips(opponent + "：思考中...");
    }

    private void showTips(String msg) {
        if (isGameOver) {
            return;
        }

        tips.setText(msg);
    }
}