package cn.xeblog.plugin.game.gobang;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.commons.entity.game.gobang.GobangDTO;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * 五子棋
 *
 * @author anlingyi
 * @date 2020/6/5
 */
@DoGame(Game.GOBANG)
public class Gobang extends AbstractGame<GobangDTO> {

    // 行数，y
    private static final int ROWS = 15;
    // 列数，x
    private static final int COLS = 15;
    // 棋子总数
    private static final int CHESS_TOTAL = ROWS * COLS;

    // 主面板
    private JPanel mainPanel;

    // 棋盘
    private JPanel chessPanel;
    // 提示
    private JLabel tips;
    // 开始界面
    private JPanel startPanel;
    // 悔棋按钮
    private JButton regretButton;
    // AI落子按钮
    private JButton aiTestButton;
    // 游戏按钮面板
    private JPanel gameButtonPanel;
    // AI自动落子
    private boolean aiAutoPut = true;

    // 每个格子的边框大小
    private int border;
    // 棋子大小
    private int chessSize;
    // 棋盘宽度
    private int width;
    // 棋盘高度
    private int height;
    // 已下棋子数据
    private int[][] chessData;
    // 当前已下棋子数
    private int currentChessTotal;
    // 棋子类型，1黑棋 2白棋
    private int type;
    // 游戏是否结束
    private boolean isGameOver;
    // 游戏状态 -2.初始化 -1.待开始 0.进行中 1.赢 2.平
    private int status = -2;
    // 标记是否已下棋子
    private boolean put;
    // 高亮棋子
    private Map<String, Boolean> chessHighlight;
    // 当前玩家名
    private String player;
    // 下一个玩家名
    private String nextPlayer;
    // 游戏模式
    private GameMode gameMode;
    // AI
    private AIService aiService;
    // AI级别
    private int aiLevel;
    // 记录落子数据
    private Stack<Point> chessStack;
    // 最后一个落子点
    private Point lastPoint;
    // AI配置
    private AIService.AIConfig aiConfig;

    // AI级别
    private static final Map<String, Integer> AI_LEVEL = new LinkedHashMap<>();

    static {
        // AI级别初始化
        AI_LEVEL.put("AI·制杖", 1);
        AI_LEVEL.put("AI·棋跪王", 2);
        AI_LEVEL.put("AI·沟流儿", 3);
        AI_LEVEL.put("AI·林必诚", 4);
    }

    /**
     * 初始化游戏数据
     */
    private void initValue() {
        lastPoint = null;
        chessData = new int[COLS][ROWS];
        currentChessTotal = 0;
        isGameOver = false;
        status = -2;
        put = false;
        chessSize = Math.round(border * 0.75f);
        width = ROWS * border + border;
        height = ROWS * border + border;
        chessStack = new Stack<>();
        initChessHighLight();
    }

    @Getter
    @AllArgsConstructor
    private enum GameMode {
        HUMAN_VS_PC("人机模式"),
        HUMAN_VS_HUMAN("左右互搏"),
        ONLINE("在线PK"),
        DEBUG("调试模式");

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
    public void handle(GobangDTO body) {
        if (status > -1) {
            setChess(new Point(body.getX(), body.getY(), body.getType()));

            if (type == 2) {
                changePlayer();
            }

            checkStatus(nextPlayer);
            if (isGameOver) {
                return;
            }
        } else {
            status = 0;
            type = body.getType();
            if (type == 2) {
                put = true;
                changePlayer();
                showTips(player + (GameAction.getNickname().equals(player) ? "(你)" : "") + "先下手为强！");
                return;
            }
        }

        put = false;
        showTips(player + "(你)：思考中...");
    }

    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (!isGameOver && status > -2) {
            showTips("游戏结束：对手逃跑了~");
            isGameOver = true;
            gameButtonPanel.add(getGameOverButton());
            gameButtonPanel.updateUI();
        }
    }

    private void checkStatus(String username) {
        boolean isDebug = gameMode == GameMode.DEBUG;
        boolean flag = true;
        switch (status) {
            case 1:
                if (!isDebug) {
                    showTips("游戏结束：" + username + "赢了！");
                }
                break;
            case 2:
                if (!isDebug) {
                    showTips("游戏结束：平局~");
                }
                break;
            default:
                flag = false;
                break;
        }

        isGameOver = flag;
        if (isGameOver && gameMode == GameMode.ONLINE) {
            gameButtonPanel.add(getGameOverButton());
            gameButtonPanel.updateUI();
        }
    }

    private void initChessPanel() {
        initValue();
        player = GameAction.getNickname();
        if (GameAction.isOfflineGame()) {
            switch (gameMode) {
                case HUMAN_VS_PC:
                    aiService = createAI();
                    if (type == 2) {
                        put = true;
                        player = nextPlayer;
                        nextPlayer = GameAction.getNickname();
                    }
                    break;
                case HUMAN_VS_HUMAN:
                    nextPlayer = "路人甲";
                    if (type == 2) {
                        type = 1;
                        player = nextPlayer;
                        nextPlayer = GameAction.getNickname();
                    }
                    break;
                case DEBUG:
                    type = 1;
                    aiLevel = 6;
                    aiService = createAI();
                    break;
            }
        } else {
            put = true;
            gameMode = GameMode.ONLINE;
            getRoom().getUsers().forEach((k, v) -> {
                if (!v.getUsername().equals(player)) {
                    nextPlayer = v.getUsername();
                    return;
                }
            });
        }

        chessPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintChessBoard(g);
            }
        };

        Dimension mainDimension = new Dimension(width + 180, height + 50);

        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
        }
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(mainDimension);

        tips = new JLabel("", JLabel.CENTER);
        tips.setFont(new Font("", Font.BOLD, 13));
        tips.setForeground(new Color(237, 81, 38));
        tips.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        chessPanel.setLayout(null);
        // 设置棋盘宽高
        chessPanel.setPreferredSize(new Dimension(width, height));
        // 设置棋盘背景颜色
        chessPanel.setBackground(Color.LIGHT_GRAY);

        JPanel topPanel = new JPanel();
        topPanel.add(tips);

        JPanel centerPanel = new JPanel();
        centerPanel.add(chessPanel);

        if (gameMode == GameMode.DEBUG) {
            JPanel rightPanel = new JPanel();
            rightPanel.add(getConfigPanel());
            mainPanel.add(rightPanel, BorderLayout.EAST);
        }

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        JPanel chessButtonPanel = new JPanel();
        gameButtonPanel = new JPanel();
        bottomPanel.add(chessButtonPanel, BorderLayout.NORTH);
        bottomPanel.add(gameButtonPanel, BorderLayout.SOUTH);
        if (gameMode == GameMode.ONLINE) {
            showTips("请等待...");
        } else {
            regretButton = getRegretButton();
            chessButtonPanel.add(regretButton);

            JButton restartButton = new JButton("再来");
            restartButton.addActionListener(e -> initChessPanel());

            JButton backButton = new JButton("返回");
            backButton.addActionListener(e -> initStartPanel());

            gameButtonPanel.add(backButton);
            gameButtonPanel.add(restartButton);
            gameButtonPanel.add(getOutputChessRecordButton());
            gameButtonPanel.add(getExitButton());
        }

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();

        boolean isDebug = gameMode == GameMode.DEBUG;
        if (isDebug) {
            showTips("调试模式");
        } else if (gameMode != GameMode.ONLINE) {
            showTips(player + (GameAction.getNickname().equals(player) ? "(你)" : "") + "先下手为强！");
        }

        if (type == 2 && gameMode == GameMode.HUMAN_VS_PC) {
            aiPutChess();
        }

        status = -1;
        chessPanel.addMouseListener(new MouseAdapter() {
            // 监听鼠标点击事件
            @Override
            public void mouseClicked(MouseEvent e) {
                if (put || isGameOver) {
                    return;
                }

                if (putChess(e.getX(), e.getY(), type)) {
                    checkStatus(player);

                    if (!isDebug) {
                        put = true;
                        boolean isOnlineMode = gameMode == GameMode.ONLINE;
                        if (!isGameOver) {
                            showTips(nextPlayer + (GameAction.getNickname().equals(nextPlayer) ? "(你)" : "") + "：思考中...");
                        } else if (!isOnlineMode) {
                            return;
                        }

                        if (!isOnlineMode || (isOnlineMode && type == 2)) {
                            changePlayer();
                        }
                    }

                    switch (gameMode) {
                        case ONLINE:
                            send(lastPoint);
                            break;
                        case HUMAN_VS_PC:
                            aiPutChess();
                            break;
                        case DEBUG:
                            if (aiTestButton != null && aiAutoPut) {
                                aiTestButton.doClick();
                                break;
                            }
                        case HUMAN_VS_HUMAN:
                            type = 3 - type;
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
        boolean isDebug = gameMode == GameMode.DEBUG;
        GlobalThreadPool.execute(() -> {
            if (setChess(aiService.getPoint(chessData, lastPoint == null ? new Point(-1, -1, type) : lastPoint))) {
                if (isDebug) {
                    showTips("调试模式");
                    type = 3 - lastPoint.type;
                }

                checkStatus(player);
                put = false;
                if (isGameOver) {
                    return;
                }

                if (!isDebug) {
                    showTips(nextPlayer + (GameAction.getNickname().equals(nextPlayer) ? "(你)" : "") + "：思考中...");
                    changePlayer();
                }
            }
        });
    }

    private JButton getRegretButton() {
        boolean isDebug = gameMode == GameMode.DEBUG;

        JButton regretButton = new JButton("悔棋");
        regretButton.setEnabled(false);
        regretButton.addActionListener(e -> {
            // 默认一次后退2步棋
            int count = 2;
            if (put || (!isDebug && isGameOver || chessStack.size() < count)) {
                return;
            }

            for (int i = 0; i < count; i++) {
                Point point = chessStack.pop();
                chessData[point.x][point.y] = 0;
            }
            this.currentChessTotal -= count;

            if (chessStack.isEmpty()) {
                type = 1;
            } else {
                // 最后一个棋子高亮
                lastPoint = chessStack.lastElement();
                type = 3 - lastPoint.type;
            }
            if (isGameOver && isDebug) {
                isGameOver = false;
                put = false;
                status = 0;
                chessHighlight = null;
            }

            chessPanel.repaint();
        });

        return regretButton;
    }

    /**
     * 输出棋谱按钮
     *
     * @return
     */
    private JButton getOutputChessRecordButton() {
        JButton exitButton = new JButton("输出棋谱");
        exitButton.addActionListener(e -> {
            if (chessStack.isEmpty()) {
                return;
            }

            ConsoleAction.showSimpleMsg("===== 棋谱输出 =====");
            StringBuffer sb = new StringBuffer();
            chessStack.forEach(p -> {
                sb.append(p.x).append(",").append(p.y).append(",").append(p.type).append(";");
            });
            ConsoleAction.showSimpleMsg(sb.toString());
            ConsoleAction.showSimpleMsg("===== END =====");
        });
        return exitButton;
    }

    /**
     * 获取配置面板
     *
     * @return
     */
    private JPanel getConfigPanel() {
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new GridLayout(8, 2));
        configPanel.setPreferredSize(new Dimension(150, 250));

        JLabel label1 = new JLabel("搜索深度：");
        label1.setFont(new Font("", 1, 13));
        configPanel.add(label1);

        JTextField searchDepthInput = new JTextField();
        searchDepthInput.setText(String.valueOf(aiConfig.getDepth()));
        searchDepthInput.setColumns(5);
        searchDepthInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) {
                    e.consume();
                }
            }
        });
        configPanel.add(searchDepthInput);

        JLabel label2 = new JLabel("启发节点：");
        label2.setFont(new Font("", 1, 13));
        configPanel.add(label2);

        JTextField maxNodesInput = new JTextField();
        maxNodesInput.setText(String.valueOf(aiConfig.getMaxNodes()));
        maxNodesInput.setColumns(5);
        maxNodesInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) {
                    e.consume();
                }
            }
        });
        configPanel.add(maxNodesInput);


        JLabel label3 = new JLabel("算杀深度：");
        label3.setFont(new Font("", 1, 13));
        configPanel.add(label3);

        JTextField vcxDepthInput = new JTextField();
        vcxDepthInput.setText(String.valueOf(aiConfig.getVcxDepth()));
        vcxDepthInput.setColumns(5);
        vcxDepthInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                int keyChar = e.getKeyChar();
                if (keyChar < KeyEvent.VK_0 || keyChar > KeyEvent.VK_9) {
                    e.consume();
                }
            }
        });
        configPanel.add(vcxDepthInput);

        JLabel label4 = new JLabel("算杀模式：");
        label4.setFont(new Font("", 1, 13));
        configPanel.add(label4);

        JRadioButton vcxCloseRadio = new JRadioButton("关闭", aiConfig.getVcx() == 0);
        vcxCloseRadio.setActionCommand("0");
        JRadioButton vctRadio = new JRadioButton("VCT", aiConfig.getVcx() == 1);
        vctRadio.setActionCommand("1");
        JRadioButton vcfRadio = new JRadioButton("VCF", aiConfig.getVcx() == 2);
        vcfRadio.setActionCommand("2");

        configPanel.add(vcxCloseRadio);
        configPanel.add(vctRadio);
        configPanel.add(vcfRadio);

        ButtonGroup vcxRadioGroup = new ButtonGroup();
        vcxRadioGroup.add(vcxCloseRadio);
        vcxRadioGroup.add(vctRadio);
        vcxRadioGroup.add(vcfRadio);

        aiTestButton = new JButton("AI落子");
        aiTestButton.addActionListener(e -> {
            if (put || isGameOver) {
                return;
            }

            put = true;
            if (lastPoint == null) {
                type = 2;
            }
            showTips("AI思考中...");

            aiConfig.setDepth(Math.min(20, Integer.parseInt(searchDepthInput.getText())));
            aiConfig.setMaxNodes(Math.min(30, Integer.parseInt(maxNodesInput.getText())));
            aiConfig.setVcx(Integer.parseInt(vcxRadioGroup.getSelection().getActionCommand()));
            aiConfig.setVcxDepth(Math.min(20, Integer.parseInt(vcxDepthInput.getText())));

            searchDepthInput.setText(String.valueOf(aiConfig.getDepth()));
            maxNodesInput.setText(String.valueOf(aiConfig.getMaxNodes()));
            vcxDepthInput.setText(String.valueOf(aiConfig.getVcxDepth()));

            aiPutChess();
        });
        configPanel.add(aiTestButton);

        JButton inputChessRecordButton = new JButton("输入棋谱");
        inputChessRecordButton.addActionListener(e -> {
            new InputChessRecordDialog().show();
        });
        configPanel.add(inputChessRecordButton);

        JCheckBox autoPutCheckBox = new JCheckBox("自动落子", aiAutoPut);
        autoPutCheckBox.addChangeListener(e -> aiAutoPut = ((JCheckBox) e.getSource()).isSelected());
        configPanel.add(autoPutCheckBox);

        return configPanel;
    }

    private static class InputChessRecordDialog extends DialogWrapper {

        private JPanel main;
        private JTextArea chessRecordText;

        InputChessRecordDialog() {
            super(true);

            setTitle("输入棋谱");
            setOKActionEnabled(true);
            setResizable(false);

            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            main = new JPanel();
            main.setPreferredSize(new Dimension(200, 200));
            chessRecordText = new JTextArea();
            chessRecordText.setColumns(20);
            chessRecordText.setRows(10);
            chessRecordText.setLineWrap(true);
            JBScrollPane scrollPane = new JBScrollPane(chessRecordText);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            main.add(scrollPane);
            return main;
        }

        @Override
        protected void doOKAction() {
            String text = chessRecordText.getText();
            if (StrUtil.isNotBlank(text)) {
                Gobang gobang = (Gobang) GameAction.getAction();
                gobang.inputChessRecode(text);
            }

            super.doOKAction();
        }
    }

    /**
     * 输入棋谱
     *
     * @param chessRecord
     */
    public void inputChessRecode(String chessRecord) {
        if (StrUtil.isBlank(chessRecord)) {
            return;
        }

        Point newLastPoint = null;
        int[][] newChessData = new int[15][15];
        Stack newChessStack = new Stack();
        int newChessTotal = 0;
        try {
            String[] chessRecords = chessRecord.split(";");
            int len = chessRecords.length;
            for (int i = 0; i < len; i++) {
                String[] point = chessRecords[i].split(",");
                int x = Integer.parseInt(point[0]);
                int y = Integer.parseInt(point[1]);
                int type = Integer.parseInt(point[2]);
                newChessData[x][y] = type;
                Point p = new Point(x, y, type);
                newChessStack.push(p);
                newChessTotal++;

                if (i == len - 1) {
                    newLastPoint = p;
                }
            }

            chessData = newChessData;
            currentChessTotal = newChessTotal;
            chessStack = newChessStack;
            lastPoint = newLastPoint;
            type = 3 - lastPoint.type;
            chessHighlight = null;
            status = 0;
            isGameOver = false;
            regretButton.setEnabled(true);

            chessPanel.repaint();
            checkWinner(lastPoint);
            checkStatus(null);
        } catch (Exception e) {
            ConsoleAction.showSimpleMsg("输入的棋谱数据无法识别！");
        }
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
            g2.drawLine(border, i * border + border, width - border, i * border + border);
        }

        // 画纵线
        for (int i = 0; i < COLS; i++) {
            g2.drawLine(i * border + border, border, i * border + border, height - border);
        }

        if (ROWS == 15 && COLS == 15) {
            // 标准棋盘
            int starSize = border / 3;
            int halfStarSize = starSize / 2;
            g2.fillOval(4 * border - halfStarSize, 4 * border - halfStarSize, starSize, starSize);
            g2.fillOval(12 * border - halfStarSize, 4 * border - halfStarSize, starSize, starSize);
            g2.fillOval(4 * border - halfStarSize, 12 * border - halfStarSize, starSize, starSize);
            g2.fillOval(12 * border - halfStarSize, 12 * border - halfStarSize, starSize, starSize);
            g2.fillOval(8 * border - halfStarSize, 8 * border - halfStarSize, starSize, starSize);
        }

        if (currentChessTotal == 0) {
            return;
        }

        // 画棋子
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
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
                int halfBorder = chessSize / 2;
                int chessX = i * border + border - halfBorder;
                int chessY = j * border + border - halfBorder;

                g2.fillOval(chessX, chessY, chessSize, chessSize);

                if (isHighlight(i, j) || i == lastPoint.x && j == lastPoint.y) {
                    // 当前棋子高亮
                    g2.setColor(Color.RED);
                    g2.drawOval(chessX, chessY, chessSize, chessSize);
                }
            }
        }
    }

    private void initStartPanel() {
        this.aiService = null;

        if (this.mainPanel == null) {
            this.mainPanel = new JPanel();
        }
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 400));

        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 400);

        mainPanel.add(startPanel);

        JLabel label1 = new JLabel("游戏模式：");
        label1.setFont(new Font("", 1, 13));
        startPanel.add(label1);

        JRadioButton humanVsPCRadio = new JRadioButton(GameMode.HUMAN_VS_PC.getName(), true);
        humanVsPCRadio.setActionCommand(humanVsPCRadio.getText());
        JRadioButton humanVsHumanRadio = new JRadioButton(GameMode.HUMAN_VS_HUMAN.getName(), false);
        humanVsHumanRadio.setActionCommand(humanVsHumanRadio.getText());
        JRadioButton debugRadio = new JRadioButton(GameMode.DEBUG.getName(), false);
        debugRadio.setActionCommand(debugRadio.getText());

        ButtonGroup modeRadioGroup = new ButtonGroup();
        modeRadioGroup.add(humanVsPCRadio);
        modeRadioGroup.add(humanVsHumanRadio);
        modeRadioGroup.add(debugRadio);

        startPanel.add(humanVsPCRadio);
        startPanel.add(humanVsHumanRadio);
        startPanel.add(debugRadio);

        JLabel label4 = new JLabel("选择AI：");
        label4.setFont(new Font("", 1, 13));
        startPanel.add(label4);

        ComboBox chessAIBox = new ComboBox();
        for (String ai : AI_LEVEL.keySet()) {
            chessAIBox.addItem(ai);
        }
        chessAIBox.setSelectedIndex(2);
        startPanel.add(chessAIBox);

        JLabel label2 = new JLabel("选择棋子：");
        label2.setFont(new Font("", 1, 13));
        startPanel.add(label2);

        JRadioButton blackChessRadio = new JRadioButton("黑棋", true);
        blackChessRadio.setActionCommand("1");
        JRadioButton whiteChessRadio = new JRadioButton("白棋", false);
        whiteChessRadio.setActionCommand("2");

        ButtonGroup chessRadioGroup = new ButtonGroup();
        chessRadioGroup.add(blackChessRadio);
        chessRadioGroup.add(whiteChessRadio);

        startPanel.add(blackChessRadio);
        startPanel.add(whiteChessRadio);

        JLabel label3 = new JLabel("棋盘尺寸：");
        label3.setFont(new Font("", 1, 13));
        startPanel.add(label3);

        ComboBox chessSizeBox = new ComboBox();
        chessSizeBox.addItem("小");
        chessSizeBox.addItem("中");
        chessSizeBox.addItem("大");
        chessSizeBox.setSelectedItem("中");
        startPanel.add(chessSizeBox);

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> {
            mainPanel.remove(startPanel);
            gameMode = GameMode.getMode(modeRadioGroup.getSelection().getActionCommand());
            type = Integer.parseInt(chessRadioGroup.getSelection().getActionCommand());
            String chessSize = chessSizeBox.getSelectedItem().toString();
            switch (chessSize) {
                case "小":
                    border = 12;
                    break;
                case "中":
                    border = 14;
                    break;
                case "大":
                    border = 16;
                    break;
            }
            if (gameMode == GameMode.HUMAN_VS_PC) {
                String ai = chessAIBox.getSelectedItem().toString();
                aiLevel = AI_LEVEL.get(ai);
                nextPlayer = ai;
            }

            initChessPanel();
        });

        startPanel.add(startGameButton);
        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            numsList.add(2);
            startPanel.add(getCreateRoomButton(numsList));
        }
        startPanel.add(getExitButton());
        mainPanel.updateUI();
    }

    @Override
    protected void init() {
        initStartPanel();
    }

    @Override
    protected void start() {
        border = 14;
        initChessPanel();

        if (getRoom() == null) {
            allPlayersGameStarted();
        }
    }

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {
            // 自旋等待一段时间，再发送游戏数据
            invoke(() -> {
                int randomType = new Random().nextInt(2) + 1;
                GobangDTO msg = new GobangDTO();
                msg.setType(3 - randomType);
                sendMsg(msg);
                handle(new GobangDTO(0, 0, randomType));
            }, 500);
        }
    }

    public boolean putChess(int x, int y, int type) {
        if (isGameOver) {
            return false;
        }

        // 计算出对应的行列 四舍五入取整
        int row = Math.round((float) (x - border) / border);
        int col = Math.round((float) (y - border) / border);

        if (row < 0 || col < 0 || row > ROWS - 1 || col > COLS - 1) {
            return false;
        }

        // 棋子圆心坐标
        int circleX = row * border + border;
        int circleY = col * border + border;

        // 判断鼠标点击的坐标是否在棋子圆外
        boolean notInCircle = Math.pow(circleX - x, 2) + Math.pow(circleY - y, 2) > Math.pow((double) chessSize / 2, 2);

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

        lastPoint = point;
        currentChessTotal++;
        chessData[point.x][point.y] = point.type;
        chessStack.push(point);

        if (regretButton != null) {
            regretButton.setEnabled(currentChessTotal > 1
                    && (gameMode == GameMode.DEBUG || gameMode == GameMode.HUMAN_VS_HUMAN || point.type != this.type));
            regretButton.requestFocus();
        }

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
        GobangDTO dto = new GobangDTO();
        dto.setX(point.x);
        dto.setY(point.y);
        dto.setType(point.type);
        sendMsg(dto);
    }

    private void showTips(String msg) {
        if (isGameOver || tips == null) {
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

    private AIService createAI() {
        boolean debug = gameMode == GameMode.DEBUG;
        aiConfig = new AIService.AIConfig(6, 10, debug, 0, 8);

        switch (aiLevel) {
            case 1:
                aiConfig.setDepth(1);
                break;
            case 2:
                aiConfig.setDepth(4);
                break;
            case 3:
                aiConfig.setDepth(6);
                aiConfig.setVcx(1);
                break;
            case 4:
                aiConfig.setDepth(8);
                aiConfig.setVcx(1);
                aiConfig.setVcxDepth(10);
                break;
        }

        return new ZhiZhangAIService(aiConfig);
    }

    @Override
    protected JPanel getComponent() {
        return this.mainPanel;
    }
}