package cn.xeblog.plugin.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author anlingyi
 * @date 2020/6/3
 */
public class Gobang {

    public static void main(String[] args) {
        JFrame gobang = new JFrame();
        gobang.setVisible(true);
        gobang.setLocationRelativeTo(null);
        gobang.setLayout(new FlowLayout());
        gobang.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gobang.setLocation(600, 100);
        gobang.setSize(new Dimension(Chess.WIDTH + 30, Chess.HEIGHT + 90));
        gobang.setResizable(false);
        JLabel tips = new JLabel("AnLingYi：思考中...", JLabel.CENTER);
        tips.setFont(new Font("微软雅黑", Font.BOLD, 13));
        tips.setForeground(new Color(237, 81, 38));
        tips.setPreferredSize(new Dimension(Chess.WIDTH + 30, 30));
        gobang.add(tips);
        gobang.add(new Chess());
    }

    static class Chess extends JPanel {

        // 每个格子的边框大小
        static final int BORDER = 10;
        // 行数
        static final int ROWS = 15;
        // 列数
        static final int COLS = 15;
        // 棋子大小，约为格子的3/4
        static final int CHESS_SIZE = Math.round(BORDER * 0.75f);
        // 棋盘宽度
        static final int WIDTH = ROWS * BORDER + BORDER;
        // 棋盘高度
        static final int HEIGHT = ROWS * BORDER + BORDER;
        // 棋子总数
        static final int CHESS_TOTAL = ROWS * COLS;

        // 已下棋子数据
        int[][] chessData;
        // 当前已下棋子数
        int currentChessTotal;
        // 是否是黑棋
        boolean isBlack;

        boolean isGameOver;

        Chess() {
            init();
        }

        /**
         * 初始化
         */
        private void init() {
            chessData = new int[ROWS][COLS];
            isBlack = false;
            currentChessTotal = 0;
            isGameOver = false;

            // 设置棋盘宽高
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            // 设置棋盘背景颜色
            setBackground(Color.LIGHT_GRAY);

            this.addMouseListener(new MouseAdapter() {
                // 监听鼠标点击事件
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (isGameOver) {
                        return;
                    }

                    // 鼠标点击的坐标
                    int x = e.getX();
                    int y = e.getY();

                    // 计算出对应的行列 向上取整
                    int row = Math.round((float)(x - BORDER) / BORDER);
                    int col = Math.round((float)(y - BORDER) / BORDER);

                    if (row < 0 || col < 0 || row > ROWS - 1 || col > COLS - 1) {
                        return;
                    }

                    // 棋子圆心坐标
                    int circleX = row * BORDER + BORDER;
                    int circleY = col * BORDER + BORDER;

                    // 判断鼠标点击的坐标是否在棋子圆外
                    boolean notInCircle = Math.pow(circleX - x, 2) + Math.pow(circleY - y, 2) > Math.pow(CHESS_SIZE / 2, 2);

                    if (notInCircle) {
                        // 不在棋子圆内
                        return;
                    }

                    if (chessData[row][col] != 0) {
                        // 此处已有棋子
                        return;
                    }

                    isBlack = !isBlack;
                    currentChessTotal++;
                    int type = isBlack ? 1 : 2;
                    chessData[row][col] = type;
                    // 重绘
                    repaint();
                    // 检查是否5连
                    checkWinner(row, col, type);
                }
            });
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
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
                    g2.setColor(Color.RED);
                    g2.drawOval(chessX,chessY, CHESS_SIZE, CHESS_SIZE);
                }
            }
        }

        /**
         * 检查是否和棋
         */
        public void checkPeace() {
            if (currentChessTotal == CHESS_TOTAL) {
                gameOver("和棋~");
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
            String msg = getChess(type) + "赢了！";

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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
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
                    gameOver(msg);
                    return;
                }
            }

            // 检查是否和棋
            checkPeace();
        }

        public String getChess(int type) {
            return type == 1 ? "黑棋" : "白棋";
        }

        /**
         * 游戏结束弹窗提醒
         *
         * @param msg
         */
        private void gameOver(String msg) {
            isGameOver = true;
            Object[] options = new Object[]{"重新开始"};
            int option = JOptionPane.showOptionDialog(this, msg, "游戏结束", JOptionPane.YES_OPTION,
                    JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
            if (option != 0) {
                return;
            }
        }
    }

}