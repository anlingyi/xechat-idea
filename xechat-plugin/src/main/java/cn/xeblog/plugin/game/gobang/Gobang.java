package cn.xeblog.plugin.game.gobang;

import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.GobangDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.game.AbstractGame;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * 五子棋
 *
 * @author anlingyi
 * @date 2020/6/5
 */
public class Gobang extends AbstractGame<GobangDTO> {

    // 棋盘
    private JPanel chessPanel;
    // 提示
    private JLabel tips;
    // 开始界面
    private JPanel startPanel;

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
    private int[][] chessData;
    // 当前已下棋子数
    private int currentChessTotal;
    // 棋子类型，1黑棋 2白棋
    private int type;
    // 游戏是否结束
    private boolean isGameOver;
    // 游戏状态 0.进行中 1.赢 2.平
    private int status;
    // 标记是否已下棋子
    private boolean put;
    // 高亮棋子
    Map<String, Boolean> chessHighlight;
    // 当前玩家名
    private String player;
    // 下一个玩家名
    private String nextPlayer;
    // 游戏模式
    private GameMode gameMode;
    // AI
    private AIService aiService;

    /**
     * 初始化游戏数据
     */
    private void initValue() {
        chessData = new int[ROWS][COLS];
        currentChessTotal = 0;
        isGameOver = false;
        status = 0;
        put = false;
        initChessHighLight();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Point {
        int x;
        int y;
        int type;
    }

    @Getter
    @AllArgsConstructor
    private enum GameMode {
        HUMAN_VS_PC("人类VS电脑"),
        HUMAN_VS_HUMAN("人类VS人类"),
        ONLINE("在线PK");

        private String name;

        public static GameMode getMode(String name) {
            for (GameMode mode : values()) {
                if (mode.name.equals(name)) {
                    return mode;
                }
            }

            return HUMAN_VS_PC;
        }
    }

    @Override
    public void handle(Response<GobangDTO> response) {
        GobangDTO gobangDTO = response.getBody();
        setChess(new Point(gobangDTO.getX(), gobangDTO.getY(), gobangDTO.getType()));

        checkStatus(nextPlayer);
        if (isGameOver) {
            return;
        }

        put = false;
        showTips(player + "(你)：思考中...");
    }

    private void checkStatus(String username) {
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

    private void initChessPanel() {
        initValue();
        player = DataCache.username;
        if (GameAction.getOpponent() == null) {
            switch (gameMode) {
                case HUMAN_VS_PC:
                    nextPlayer = "人工制杖";
                    aiService = new ZhiZhangAIService();
                    if (type == 2) {
                        put = true;
                        player = nextPlayer;
                        nextPlayer = DataCache.username;
                    }
                    break;
                case HUMAN_VS_HUMAN:
                    nextPlayer = "路人甲";
                    if (type == 2) {
                        type = 1;
                        player = nextPlayer;
                        nextPlayer = DataCache.username;
                    }
                    break;
            }
        } else {
            nextPlayer = GameAction.getOpponent();
            gameMode = GameMode.ONLINE;
            if (GameAction.isProactive()) {
                type = 1;
            } else {
                type = 2;
                put = true;
            }
        }

        chessPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintChessBoard(g);
            }
        };

        Dimension mainDimension = new Dimension(WIDTH + 50, HEIGHT + 50);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.setPreferredSize(mainDimension);

        tips = new JLabel("", JLabel.CENTER);
        tips.setFont(new Font("", Font.BOLD, 13));
        tips.setForeground(new Color(237, 81, 38));
        tips.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // 设置棋盘宽高
        chessPanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        // 设置棋盘背景颜色
        chessPanel.setBackground(Color.LIGHT_GRAY);

        JPanel topPanel = new JPanel();
        topPanel.add(tips);

        JPanel centerPanel = new JPanel();
        centerPanel.add(chessPanel);

        JPanel bottomPanel = new JPanel();
        if (gameMode != GameMode.ONLINE) {
            JButton restartButton = new JButton("重新开始");
            restartButton.addActionListener(e -> {
                mainPanel.removeAll();
                initStartPanel();
                mainPanel.updateUI();
            });
            bottomPanel.add(restartButton);
        }
        bottomPanel.add(getExitButton());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        showTips(player + (DataCache.username.equals(player) ? "(你)" : "") + "先下手为强！");

        if (type == 2 && gameMode == GameMode.HUMAN_VS_PC) {
            aiPutChess();
        }

        chessPanel.addMouseListener(new MouseAdapter() {
            // 监听鼠标点击事件
            @Override
            public void mouseClicked(MouseEvent e) {
                if (put || isGameOver) {
                    return;
                }

                if (putChess(e.getX(), e.getY(), type)) {
                    put = true;
                    checkStatus(player);

                    if (isGameOver) {
                        return;
                    }

                    showTips(nextPlayer + (DataCache.username.equals(nextPlayer) ? "(你)" : "") + "：思考中...");

                    switch (gameMode) {
                        case ONLINE:
                            send(new Point(currentX, currentY, type));
                            break;
                        case HUMAN_VS_PC:
                            changePlayer();
                            aiPutChess();
                            break;
                        case HUMAN_VS_HUMAN:
                            type = 3 - type;
                            changePlayer();
                            put = false;
                            break;
                    }
                }
            }
        });
    }

    private void changePlayer() {
        String tempName = player;
        player = nextPlayer;
        nextPlayer = tempName;
    }

    private void aiPutChess() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (setChess(aiService.getPoint(chessData, new Point(currentX, currentY, type)))) {
                checkStatus(player);

                if (isGameOver) {
                    return;
                }

                put = false;
                showTips(nextPlayer + (DataCache.username.equals(nextPlayer) ? "(你)" : "") + "：思考中...");
                changePlayer();
            }
        }).start();
    }

    private JButton getExitButton() {
        JButton exitButton = new JButton("退出游戏");
        exitButton.addActionListener(e -> Command.GAME_OVER.exec(null));
        return exitButton;
    }

    /**
     * 绘制棋盘
     *
     * @param g
     */
    private void paintChessBoard(Graphics g) {
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

        if (ROWS == 15 && COLS == 15) {
            // 标准棋盘
            int starSize = BORDER / 3;
            int halfStarSize = starSize / 2;
            g2.fillOval(4 * BORDER - halfStarSize, 4 * BORDER - halfStarSize, starSize, starSize);
            g2.fillOval(12 * BORDER - halfStarSize, 4 * BORDER - halfStarSize, starSize, starSize);
            g2.fillOval(4 * BORDER - halfStarSize, 12 * BORDER - halfStarSize, starSize, starSize);
            g2.fillOval(12 * BORDER - halfStarSize, 12 * BORDER - halfStarSize, starSize, starSize);
            g2.fillOval(8 * BORDER - halfStarSize, 8 * BORDER - halfStarSize, starSize, starSize);
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

                if (isHighlight(i, j) || i == currentX && j == currentY) {
                    // 当前棋子高亮
                    g2.setColor(Color.RED);
                    g2.drawOval(chessX, chessY, CHESS_SIZE, CHESS_SIZE);
                }
            }
        }
    }

    private void initStartPanel() {
        if (GameAction.getOpponent() != null) {
            initChessPanel();
            return;
        }

        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(150, 300));

        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 240);

        mainPanel.add(startPanel);

        JLabel label1 = new JLabel("游戏模式：");
        label1.setFont(new Font("", 1, 13));
        startPanel.add(label1);

        JRadioButton humanVsPCRadio = new JRadioButton(GameMode.HUMAN_VS_PC.getName(), true);
        humanVsPCRadio.setActionCommand(humanVsPCRadio.getText());
        JRadioButton humanVsHumanRadio = new JRadioButton(GameMode.HUMAN_VS_HUMAN.getName(), false);
        humanVsHumanRadio.setActionCommand(humanVsHumanRadio.getText());

        ButtonGroup modeRadioGroup = new ButtonGroup();
        modeRadioGroup.add(humanVsPCRadio);
        modeRadioGroup.add(humanVsHumanRadio);

        startPanel.add(humanVsPCRadio);
        startPanel.add(humanVsHumanRadio);

        JLabel label2 = new JLabel("选择棋子：");
        label2.setFont(new Font("", 1, 13));
        startPanel.add(label2);

        JRadioButton blackChessRadio = new JRadioButton("黑棋", true);
        blackChessRadio.setActionCommand("1");
        JRadioButton whiteChessRadio = new JRadioButton("白棋");
        whiteChessRadio.setActionCommand("2");

        ButtonGroup chessRadioGroup = new ButtonGroup();
        chessRadioGroup.add(blackChessRadio);
        chessRadioGroup.add(whiteChessRadio);

        startPanel.add(blackChessRadio);
        startPanel.add(whiteChessRadio);

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> {
            mainPanel.remove(startPanel);
            gameMode = GameMode.getMode(modeRadioGroup.getSelection().getActionCommand());
            type = Integer.parseInt(chessRadioGroup.getSelection().getActionCommand());
            initChessPanel();
        });

        startPanel.add(startGameButton);
        startPanel.add(getExitButton());
    }

    @Override
    protected void init() {
        initStartPanel();
    }

    @Override
    public void start() {
        super.start();
    }

    private int currentX;
    private int currentY;

    public boolean putChess(int x, int y, int type) {
        if (isGameOver) {
            return false;
        }

        // 计算出对应的行列 四舍五入取整
        int row = Math.round((float) (x - BORDER) / BORDER);
        int col = Math.round((float) (y - BORDER) / BORDER);

        if (row < 0 || col < 0 || row > ROWS - 1 || col > COLS - 1) {
            return false;
        }

        // 棋子圆心坐标
        int circleX = row * BORDER + BORDER;
        int circleY = col * BORDER + BORDER;

        // 判断鼠标点击的坐标是否在棋子圆外
        boolean notInCircle = Math.pow(circleX - x, 2) + Math.pow(circleY - y, 2) > Math.pow((double) CHESS_SIZE / 2, 2);

        if (notInCircle) {
            // 不在棋子圆内
            return false;
        }

        return setChess(new Point(row, col, type));
    }

    private boolean setChess(Point point) {
        if (chessData[point.x][point.y] != 0) {
            // 此处已有棋子
            return false;
        }

        currentX = point.x;
        currentY = point.y;
        currentChessTotal++;
        chessData[point.x][point.y] = point.type;

        // 重绘
        chessPanel.repaint();

        // 检查是否5连
        checkWinner(point);

        return true;
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
     * @param point
     */
    public void checkWinner(Point point) {
        int x = point.x;
        int y = point.y;
        int type = point.type;

        // 横轴
        initChessHighLight();
        int k = 1;
        for (int i = 1; i < 5; i++) {
            int preX = x - i;
            if (preX < 0) {
                break;
            }

            if (chessData[preX][y] != type) {
                break;
            }

            setChessHighlight(preX, y);

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

            setChessHighlight(nextX, y);

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 纵轴
        initChessHighLight();
        k = 1;
        for (int i = 1; i < 5; i++) {
            int preY = y - i;
            if (preY < 0) {
                break;
            }

            if (chessData[x][preY] != type) {
                break;
            }

            setChessHighlight(x, preY);

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

            setChessHighlight(x, nextY);

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 左对角线
        initChessHighLight();
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

            setChessHighlight(preX, preY);

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

            setChessHighlight(nextX, nextY);

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 右对角线
        initChessHighLight();
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

            setChessHighlight(nextX, preY);

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

            setChessHighlight(preX, nextY);

            if (++k == 5) {
                winner();
                return;
            }
        }

        // 检查是否和棋
        checkPeace();

        initChessHighLight();
    }

    private void winner() {
        chessPanel.repaint();
        status = 1;
    }

    private void peacemaker() {
        status = 2;
    }

    private void send(Point point) {
        String opponent = GameAction.getOpponent();
        GobangDTO dto = new GobangDTO();
        dto.setX(point.x);
        dto.setY(point.y);
        dto.setType(point.type);
        dto.setOpponentId(DataCache.userMap.get(opponent));
        MessageAction.send(dto, Action.GAME);
    }

    private void showTips(String msg) {
        if (isGameOver) {
            return;
        }

        tips.setText(msg);
    }

    private void initChessHighLight() {
        chessHighlight = new HashMap<>();
    }

    private void setChessHighlight(int x, int y) {
        this.chessHighlight.put(x + "," + y, true);
    }

    private boolean isHighlight(int x, int y) {
        if (chessHighlight == null) {
            return false;
        }

        return chessHighlight.containsKey(x + "," + y);
    }
}