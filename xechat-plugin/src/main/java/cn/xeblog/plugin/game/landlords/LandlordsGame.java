package cn.xeblog.plugin.game.landlords;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.landlords.AllocPokerDTO;
import cn.xeblog.commons.entity.game.landlords.LandlordsGameDTO;
import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBScrollPane;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author anlingyi
 * @date 2022/6/2 1:13 下午
 */
@DoGame(Game.LANDLORDS)
public class LandlordsGame extends AbstractGame<LandlordsGameDTO> {

    public static DebugMode debugMode;

    private AtomicInteger restartCounter;
    private JPanel startPanel;
    private JButton gameOverButton;
    private JButton outPokerButton;
    private JButton notOutPokerButton;
    private JButton notCallScoreButton;
    private JButton callScoreButton;
    private JLabel tipsLabel;
    private JPanel outPokerPanel;
    private JPanel playerTopPanel;
    private JLabel titleLabel;
    private JButton backButton;
    private JPanel pokerListPanel;

    private Map<String, Player> playerMap;

    private static List<String> aiPlayerList;

    private Map<String, PlayerAction> aiPlayerActionMap;

    private List<String> userList;

    static {
        aiPlayerList = new ArrayList<>();
        aiPlayerList.add("AI·傻瓜蛋");
        aiPlayerList.add("AI·小傻子");
        aiPlayerList.add("AI·小笨蛋");
    }

    /**
     * 状态 0.初始化 1.摸牌 2.叫分 3.出牌 4.失败 5.胜利
     */
    private int state;

    /**
     * 当前玩家
     */
    private PlayerNode currentPlayer;

    /**
     * 玩家手牌
     */
    private List<Poker> pokers;

    /**
     * 底牌
     */
    private List<Poker> lastPokers;

    /**
     * 已选中的牌
     */
    private List<Poker> selectedPokers;

    /**
     * 当前出牌信息
     */
    private PokerInfo pokerInfo;

    /**
     * 上家出牌信息
     */
    private PokerInfo lastPokerInfo;

    /**
     * 上家出牌玩家
     */
    private String lastPlayer;

    /**
     * 接收计数器
     */
    private AtomicInteger receiveCounter;

    /**
     * 叫分最大的玩家
     */
    private Player maxScorePlayer;

    /**
     * 最大叫分
     */
    private int maxScore;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private class Player {
        private PlayerNode playerNode;
        private JPanel panel;
        private JLabel tipsLabel;
        private JLabel roleLabel;
        private JLabel nicknameLabel;

        public Player(PlayerNode playerNode, JPanel panel) {
            this.playerNode = playerNode;
            this.panel = panel;
        }

        public void showTips(String tips) {
            tipsLabel.setText(tips);
            tipsLabel.updateUI();
        }

        public void flushRole() {
            roleLabel.setText(playerNode.getRoleInfo() + "：" + playerNode.getPokerTotal());
            roleLabel.updateUI();
        }

        public void flushNickname() {
            if (isHard()) {
                nicknameLabel.setText(playerNode.getAlias());
            } else {
                nicknameLabel.setText(playerNode.getPlayer());
            }
            nicknameLabel.updateUI();
        }
    }

    private void initValue() {
        state = 0;
        maxScore = 0;
        pokers = null;
        lastPokers = null;
        pokerInfo = null;
        lastPokerInfo = null;
        lastPlayer = null;
        currentPlayer = null;
        maxScorePlayer = null;
        playerMap = null;
        selectedPokers = new ArrayList<>();
        receiveCounter = new AtomicInteger();
        userList = new ArrayList<>();
        aiPlayerActionMap = new HashMap<>();
    }

    @Override
    protected void init() {
        state = 0;
        if (restartCounter == null) {
            restartCounter = new AtomicInteger();
        }
        restartCounter.incrementAndGet();

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 200));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("斗地主！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getStartGameButton());
        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            numsList.add(2);
            numsList.add(3);
            vBox.add(getCreateRoomButton(numsList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    protected void start() {
        initValue();
        if (gameRoom != null) {
            userList.addAll(gameRoom.getUsers().keySet());
        } else {
            userList.add(GameAction.getNickname());
        }

        buildPlayerNode();
        showGamePanel();
        state = 1;
        showTips("请等待...");

        int usersTotal = userList.size();
        if (isHomeowner) {
            spinMoment(100);
            int nums = 3 - usersTotal;
            if (nums > 0) {
                List<String> joinedAIList = new ArrayList<>(aiPlayerList);
                joinedAIList.removeAll(userList);
                Collections.shuffle(joinedAIList);
                List<String> aiList = joinedAIList.subList(0, nums);
                aiList.forEach(ai -> aiPlayerActionMap.put(ai, null));
                sendMsg(LandlordsGameDTO.MsgType.JOIN_ROBOTS, GameAction.getNickname(), new ArrayList<>(aiList));
            } else {
                allocPokersMsg();
            }
        }
    }

    private void allocPokersMsg() {
        int priority = RandomUtil.randomInt(3);
        List<List<Poker>> allocPokers = PokerUtil.allocPokers();
        List<String> playerList = userList;
        for (int i = 0; i < playerList.size(); i++) {
            sendMsg(LandlordsGameDTO.MsgType.ALLOC_POKER, playerList.get(i),
                    new AllocPokerDTO(allocPokers.get(i), allocPokers.get(3), priority == i));
        }
    }

    private void sendMsg(LandlordsGameDTO.MsgType msgType, Object data) {
        sendMsg(msgType, currentPlayer.getPlayer(), data);
    }

    private void sendMsg(LandlordsGameDTO.MsgType msgType, String player, Object data) {
        if (state == 0) {
            return;
        }

        LandlordsGameDTO dto = new LandlordsGameDTO();
        dto.setMsgType(msgType);
        dto.setPlayer(player);
        dto.setData(data);
        if (gameRoom != null) {
            sendMsg(dto);
        }
        invoke(() -> handle(dto));
    }

    private void controlRobotCallScore(PlayerNode playerNode, int score) {
        invokeRobot(() -> sendMsg(LandlordsGameDTO.MsgType.CALL_SCORE, playerNode.getPlayer(),
                aiPlayerActionMap.get(playerNode.getPlayer()).callScore(score)));
    }

    private void controlRobotOutPoker(PlayerNode playerNode, PlayerNode outPlayer, PokerInfo outPokerInfo) {
        invokeRobot(() -> sendMsg(LandlordsGameDTO.MsgType.OUT_POKER, playerNode.getPlayer(),
                aiPlayerActionMap.get(playerNode.getPlayer()).outPoker(outPlayer, outPokerInfo)));
    }

    private void invokeRobot(Runnable runnable) {
        int restart = restartCounter.get();
        invoke(() -> {
            if (restart != restartCounter.get()) {
                return;
            }

            runnable.run();
        }, 1800);
    }

    @Override
    public void handle(LandlordsGameDTO body) {
        if (state == 0) {
            return;
        }

        Player player = playerMap.get(body.getPlayer());
        PlayerNode playerNode = player.getPlayerNode();
        PlayerNode nextPlayerNode = playerNode.getNextPlayer();
        Player nextPlayer = null;
        PlayerAction aiPlayerAction = null;
        if (nextPlayerNode != null) {
            nextPlayer = playerMap.get(nextPlayerNode.getPlayer());
            aiPlayerAction = aiPlayerActionMap.get(nextPlayerNode.getPlayer());
        }
        boolean isMe = playerNode == currentPlayer;
        boolean controlRobot = isHomeowner && aiPlayerAction != null;
        boolean isHard = isHard();

        switch (body.getMsgType()) {
            case JOIN_ROBOTS:
                List<String> robotList = (List<String>) body.getData();
                userList.addAll(robotList);
                buildPlayerNode();
                showGamePanel();
                showTips("等待发牌...");
                if (isHomeowner) {
                    spinMoment(100);
                    allocPokersMsg();
                }
                break;
            case ALLOC_POKER:
                if (receivedAndOk()) {
                    state = 2;
                }

                AllocPokerDTO allocPokerDTO = (AllocPokerDTO) body.getData();
                List<Poker> allocPokerList = allocPokerDTO.getPokers();
                playerNode.setPokerTotal(allocPokerList.size());
                if (isMe) {
                    controlRobot = false;
                    pokers = allocPokerList;
                    lastPokers = allocPokerDTO.getLastPokers();
                    showTips(isHard ? "Confirming Master Machine..." : "正在确定地主...");
                } else {
                    aiPlayerAction = aiPlayerActionMap.get(playerNode.getPlayer());
                    controlRobot = aiPlayerAction != null && isHomeowner;
                }

                if (controlRobot) {
                    aiPlayerAction.setPokers(allocPokerList);
                }

                flushPlayerPanel(player);

                if (allocPokerDTO.isPrioritized()) {
                    if (isMe) {
                        spinMoment(100);
                        showCallScoreButton(0);
                    } else {
                        player.showTips(isHard ? "Priority!" : "优先叫地主！");
                        if (controlRobot) {
                            controlRobotCallScore(playerNode, 0);
                        }
                    }
                }
                break;
            case CALL_SCORE:
                String tips;
                int score = (int) body.getData();
                if (score == 0) {
                    tips = isHard ? "Abandon!" : "不叫！";
                } else {
                    tips = score + (isHard ? " Point!" : "分！");
                    if (score > maxScore) {
                        maxScore = score;
                        maxScorePlayer = player;
                    }
                }

                if (isMe) {
                    showPlayerTips(tips);
                } else {
                    player.showTips(tips);
                }

                boolean isOk = receivedAndOk();
                if (isOk && maxScorePlayer == null) {
                    state = 1;
                    showTips(isHard ? "Master Machine Not Confirmed! Once Again..." : "未确定地主！正在重新发牌...");
                    if (isHomeowner) {
                        int restartValue = restartCounter.get();
                        invoke(() -> {
                            if (restartCounter.get() != restartValue) {
                                return;
                            }

                            allocPokersMsg();
                        }, 2000);
                    }
                    break;
                }

                if (!isOk && score == 3 || isOk && maxScorePlayer != null) {
                    state = 3;

                    PlayerNode maxScorePlayerNode = maxScorePlayer.getPlayerNode();
                    maxScorePlayerNode.setRole(2);
                    maxScorePlayerNode.setPokerTotal(playerNode.getPokerTotal() + 3);
                    boolean masterIsMe = maxScorePlayerNode == currentPlayer;
                    if (isHard) {
                        showTips(maxScorePlayerNode.getAlias() + (masterIsMe ? " (me)" : "") + " is the master！");
                    } else {
                        showTips(maxScorePlayerNode.getPlayer() + (masterIsMe ? "(你)" : "") + "成为地主！");
                    }
                    showPokers(lastPokers);

                    PlayerNode maxScorePlayerPrevPlayerNode = maxScorePlayerNode.getPrevPlayer();
                    PlayerNode maxScorePlayerNextPlayerNode = maxScorePlayerNode.getNextPlayer();
                    maxScorePlayerPrevPlayerNode.setRole(1);
                    maxScorePlayerNextPlayerNode.setRole(1);

                    Player masterPrevPlayer = playerMap.get(maxScorePlayerPrevPlayerNode.getPlayer());
                    Player masterNextPlayer = playerMap.get(maxScorePlayerNextPlayerNode.getPlayer());
                    masterPrevPlayer.flushRole();
                    masterNextPlayer.flushRole();
                    if (maxScorePlayerPrevPlayerNode != currentPlayer) {
                        masterPrevPlayer.showTips("");
                    }
                    if (maxScorePlayerNextPlayerNode != currentPlayer) {
                        masterNextPlayer.showTips("");
                    }

                    Player maxScorePlayer = playerMap.get(maxScorePlayerNode.getPlayer());
                    if (maxScorePlayerNode == currentPlayer) {
                        pokers.addAll(lastPokers);
                        PokerUtil.sorted(pokers, true);
                        flushPlayerPanel(maxScorePlayer);
                        spinMoment(100);
                        showOutPokerButton(true);
                    } else {
                        this.maxScorePlayer.showTips(isHard ? "Debugging..." : "思考中...");
                        maxScorePlayer.flushRole();
                        aiPlayerAction = aiPlayerActionMap.get(maxScorePlayerNode.getPlayer());
                        if (isHomeowner && aiPlayerAction != null) {
                            aiPlayerAction.setLastPokers(lastPokers);
                            controlRobotOutPoker(maxScorePlayerNode, null, null);
                        }
                    }
                } else {
                    if (nextPlayer.getPlayerNode() == currentPlayer) {
                        showCallScoreButton(maxScore);
                    } else {
                        nextPlayer.showTips(isHard ? "Confirm..." : "叫分中...");
                        if (controlRobot) {
                            controlRobotCallScore(nextPlayerNode, maxScore);
                        }
                    }
                }
                break;
            case OUT_POKER:
                PokerInfo outPokerInfo = (PokerInfo) body.getData();
                if (outPokerInfo == null) {
                    String tips2 = isHard ? "Run!" : "过！";
                    if (isMe) {
                        showPlayerTips(tips2);
                    } else {
                        player.showTips(tips2);
                    }
                } else {
                    String pokerModeInfo = getPokerDisplayModel(outPokerInfo);
                    if (isMe) {
                        showPlayerTips(pokerModeInfo);
                    } else {
                        player.showTips(pokerModeInfo);
                    }

                    String lastPlayerRole = playerNode.getRoleInfo();
                    lastPlayer = playerNode.getPlayer();
                    lastPokerInfo = outPokerInfo;
                    boolean isOver = playerNode.minusPoker(outPokerInfo.getPokers().size()) == 0;
                    if (isHard) {
                        String hardTips = "[" + lastPlayerRole + "] " + playerNode.getAlias() + (isMe ? " (me)" : "") + " debug result";
                        showTips(hardTips);
                    } else {
                        showTips("[" + lastPlayerRole + "]" + lastPlayer + (isMe ? "(你)" : "") + "已出牌");
                    }
                    showPokerInfo(lastPokerInfo);
                    player.flushRole();
                    if (isOver) {
                        showGameOver(playerNode.getRole());
                        return;
                    }
                }

                if (nextPlayerNode == currentPlayer) {
                    showOutPokerButton(false);
                } else {
                    playerMap.get(nextPlayerNode.getPlayer()).showTips(isHard ? "Debugging..." : "思考中...");
                }
                if (controlRobot) {
                    PlayerNode lastPlayerNode = lastPlayer == null ? null : playerMap.get(lastPlayer).getPlayerNode();
                    controlRobotOutPoker(nextPlayerNode, lastPlayerNode, lastPokerInfo);
                }
                break;
        }
    }

    private void showGameOver(int role) {
        boolean isHard = isHard();
        String roleName = role == 1 ? (isHard ? "Slave" : "农民") : (isHard ? "Master" : "地主");
        state = role == currentPlayer.getRole() ? 5 : 4;
        showTips(roleName + (isHard ? " Victory!" : "胜利！"));
        if (gameRoom != null) {
            gameOverButton.setVisible(true);
        }
    }

    private void showGamePanel() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(490, 350));

        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(480, 300));
        panel.setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel();
        JPanel rightPanel = new JPanel();
        JPanel bottomPanel = new JPanel();
        JPanel centerPanel = new JPanel();

        JPanel textPanel = new JPanel();
        tipsLabel = new JLabel();
        tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tipsLabel.setPreferredSize(new Dimension(200, 40));
        tipsLabel.setForeground(playerTipsColor);
        textPanel.add(tipsLabel);
        centerPanel.add(textPanel);

        JPanel showPokerPanel = new JPanel(new BorderLayout());
        showPokerPanel.setPreferredSize(new Dimension(200, 70));
        outPokerPanel = new JPanel();
        JBScrollPane pokersScroll = new JBScrollPane(outPokerPanel);
        pokersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pokersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        showPokerPanel.add(pokersScroll);
        centerPanel.add(showPokerPanel);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        playerMap.get(currentPlayer.getPlayer()).setPanel(bottomPanel);
        if (currentPlayer.getPrevPlayer() != null) {
            playerMap.get(currentPlayer.getPrevPlayer().getPlayer()).setPanel(leftPanel);
        }
        if (currentPlayer.getNextPlayer() != null) {
            playerMap.get(currentPlayer.getNextPlayer().getPlayer()).setPanel(rightPanel);
        }
        playerMap.forEach((k, v) -> flushPlayerPanel(v));

        JPanel mainTopPanel = new JPanel();
        String title = "斗地主！";
        if (isHard()) {
            title = "Synergy Debugging";
        } else if (debugMode == DebugMode.SOFT) {
            title = "";
        }
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("", 1, 14));
        mainTopPanel.add(titleLabel);

        JPanel mainBottomPanel = new JPanel();
        if (gameRoom == null) {
            backButton = getBackButton();
            mainBottomPanel.add(backButton);
        }
        gameOverButton = getGameOverButton();
        gameOverButton.setVisible(false);
        mainBottomPanel.add(gameOverButton);

        Box hBox = Box.createHorizontalBox();
        hBox.add(Box.createHorizontalStrut(100));
        JLabel debugModeLabel = new JLabel("Debug Mode: ");
        hBox.add(debugModeLabel);
        hBox.add(getDebugModelComboBox());
        mainBottomPanel.add(hBox);

        mainPanel.add(mainTopPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setMinimumSize(new Dimension(5, 0));
        mainPanel.add(placeholderPanel, BorderLayout.WEST);
        mainPanel.add(placeholderPanel, BorderLayout.EAST);
        mainPanel.updateUI();

        flushDebugMode();
    }

    private Border pokerBorder = BorderFactory.createLineBorder(new Color(0, 0, 0));
    private Border pokerSelectedBorder = BorderFactory.createLineBorder(new Color(255, 104, 104));
    private Color playerTipsColor = new Color(241, 135, 135);

    private void flushPlayerPanel(Player player) {
        boolean isHard = isHard();
        PlayerNode playerNode = player.getPlayerNode();
        JPanel panel = player.getPanel();
        panel.removeAll();

        String nickname = isHard ? playerNode.getAlias() : playerNode.getPlayer();
        JLabel nicknameLabel = new JLabel("<html>" + nickname + "</html>");
        JLabel roleLabel = new JLabel();
        JPanel mainPanel = new JPanel();

        if (playerNode == currentPlayer) {
            mainPanel.setPreferredSize(new Dimension(460, 150));

            playerTopPanel = new JPanel();
            playerTopPanel.setPreferredSize(new Dimension(460, 40));
            panel.add(playerTopPanel);

            JPanel pokerMainPanel = new JPanel(new BorderLayout());
            pokerMainPanel.setPreferredSize(new Dimension(460, 70));
            pokerListPanel = new JPanel();
            flushPokers();

            JBScrollPane pokersScroll = new JBScrollPane(pokerListPanel);
            pokersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pokersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            pokerMainPanel.add(pokersScroll);
            mainPanel.add(pokerMainPanel);
            nicknameLabel.setText(nickname);
            nicknameLabel.setPreferredSize(new Dimension(300, 30));
            mainPanel.add(nicknameLabel);
            mainPanel.add(roleLabel);
        } else {
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setPreferredSize(new Dimension(120, 100));
            mainPanel.setBorder(BorderFactory.createEtchedBorder());
            nicknameLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nicknameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
            mainPanel.add(nicknameLabel, BorderLayout.NORTH);
            int alignment;
            Border border;
            if (currentPlayer.getNextPlayer() == playerNode) {
                alignment = SwingConstants.LEFT;
                border = BorderFactory.createEmptyBorder(0, 5, 5, 0);
            } else {
                alignment = SwingConstants.RIGHT;
                border = BorderFactory.createEmptyBorder(0, 0, 5, 5);
            }
            roleLabel.setBorder(border);
            roleLabel.setHorizontalAlignment(alignment);
            mainPanel.add(roleLabel, BorderLayout.SOUTH);

            JLabel tipsLabel = new JLabel("");
            tipsLabel.setForeground(playerTipsColor);
            tipsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            player.setTipsLabel(tipsLabel);
            mainPanel.add(tipsLabel, BorderLayout.CENTER);
        }

        player.setNicknameLabel(nicknameLabel);
        player.setRoleLabel(roleLabel);
        player.flushRole();

        panel.add(mainPanel);
        panel.updateUI();
    }

    private void flushPokers() {
        if (pokers != null && pokerListPanel != null) {
            pokerListPanel.removeAll();
            pokers.forEach(poker -> {
                JPanel pokerPanel = getPokerPanel(poker);
                pokerListPanel.add(pokerPanel);
                if (selectedPokers.contains(poker)) {
                    pokerPanel.setBorder(pokerSelectedBorder);
                } else {
                    pokerPanel.setBorder(pokerBorder);
                }

                pokerPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (state != 3) {
                            return;
                        }

                        boolean selected = selectedPokers.remove(poker);
                        if (selected) {
                            pokerPanel.setBorder(pokerBorder);
                        } else {
                            selectedPokers.add(poker);
                            pokerPanel.setBorder(pokerSelectedBorder);
                        }

                        pokerInfo = PokerUtil.getPokerInfo(selectedPokers);
                        if (pokerInfo != null) {
                            if (!currentPlayer.getPlayer().equals(lastPlayer) && !pokerInfo.biggerThanIt(lastPokerInfo)) {
                                pokerInfo = null;
                            }
                        }

                        if (outPokerButton != null) {
                            outPokerButton.setEnabled(pokerInfo != null);
                        }
                    }
                });
            });
            pokerListPanel.updateUI();
        }
    }

    private void showOutPokerButton(boolean started) {
        boolean isHard = isHard();
        boolean lastIsMe = currentPlayer.getPlayer().equals(lastPlayer);
        if (lastIsMe) {
            lastPokerInfo = null;
        }

        playerTopPanel.removeAll();
        outPokerButton = new JButton(isHard ? "Debug" : "出牌");
        outPokerButton.setEnabled(pokerInfo != null && pokerInfo.biggerThanIt(lastPokerInfo));
        outPokerButton.addActionListener(e -> {
            if (pokerInfo != null) {
                PokerInfo copy = BeanUtil.copyProperties(pokerInfo, PokerInfo.class);
                outPokerButton.setEnabled(false);
                pokers.removeAll(selectedPokers);
                selectedPokers.clear();
                flushPlayerPanel(playerMap.get(currentPlayer.getPlayer()));
                sendMsg(LandlordsGameDTO.MsgType.OUT_POKER, copy);
                pokerInfo = null;
            }
        });

        if (!started && !lastIsMe) {
            notOutPokerButton = new JButton(isHard ? "Run!" : "过！");
            notOutPokerButton.addActionListener(e -> {
                sendMsg(LandlordsGameDTO.MsgType.OUT_POKER, null);
            });

            playerTopPanel.add(notOutPokerButton);
        }

        playerTopPanel.add(outPokerButton);
        playerTopPanel.updateUI();
    }

    private void showCallScoreButton(int score) {
        boolean isHard = isHard();
        playerTopPanel.removeAll();
        notCallScoreButton = new JButton(isHard ? "Abandon!" : "不叫");
        notCallScoreButton.addActionListener(e -> {
            if (state != 2) {
                return;
            }
            sendMsg(LandlordsGameDTO.MsgType.CALL_SCORE, 0);
        });
        playerTopPanel.add(notCallScoreButton);

        for (int i = score + 1; i < 4; i++) {
            int callScore = i;
            callScoreButton = new JButton(callScore + (isHard ? " Point" : "分"));
            callScoreButton.addActionListener(e -> {
                if (state != 2) {
                    return;
                }

                sendMsg(LandlordsGameDTO.MsgType.CALL_SCORE, callScore);
            });
            playerTopPanel.add(callScoreButton);
        }

        playerTopPanel.updateUI();
    }

    private void showPlayerTips(String tips) {
        playerTopPanel.removeAll();
        JLabel label = new JLabel(tips);
        label.setForeground(playerTipsColor);
        playerTopPanel.add(label);
        playerTopPanel.updateUI();
    }

    private JPanel getPokerPanel(Poker poker) {
        boolean isHard = isHard();
        boolean isNotDefault = debugMode != null && debugMode != DebugMode.DEFAULT;
        JPanel pokerPanel = new JPanel();
        pokerPanel.setBorder(pokerBorder);
        pokerPanel.setBackground(isNotDefault ? null : new Color(217, 214, 214));
        pokerPanel.setPreferredSize(new Dimension(30, 55));
        pokerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int value = poker.getValue();
        Poker.Suits suits = poker.getSuits();
        Color pokerColor;
        if (suits == Poker.Suits.SPADE || suits == Poker.Suits.CLUB || value == 16) {
            pokerColor = isNotDefault ? null : new Color(0, 0, 0);
        } else {
            pokerColor = isNotDefault ? null : new Color(252, 78, 78);
        }

        String displayValue = getPokerDisplayValue(value);
        JLabel pokerValue = new JLabel(" " + (isHard ? getPokerDisplayValueForHard(value) : displayValue));
        pokerValue.setForeground(pokerColor);
        pokerValue.setPreferredSize(new Dimension(30, 12));

        JLabel pokerSuits = new JLabel(" " + getPokerDisplaySuits(suits));
        pokerSuits.setForeground(pokerColor);
        pokerSuits.setPreferredSize(new Dimension(30, 10));

        pokerPanel.setToolTipText(displayValue);
        pokerPanel.add(pokerValue);
        pokerPanel.add(pokerSuits);
        return pokerPanel;
    }

    private void showTips(String tips) {
        tipsLabel.setText("<html>" + tips + "</html>");
        tipsLabel.updateUI();
    }

    private void showPokerInfo(PokerInfo pokerInfo) {
        showPokers(pokerInfo.getPokers());
    }

    private void showPokers(List<Poker> pokerList) {
        outPokerPanel.removeAll();
        if (pokerList != null) {
            pokerList.forEach(poker -> outPokerPanel.add(getPokerPanel(poker)));
        }
        outPokerPanel.updateUI();
    }

    private String getPokerDisplayModel(PokerInfo pokerInfo) {
        boolean isHard = isHard();
        if (pokerInfo != null) {
            String minValue = getPokerDisplayValue(pokerInfo.getPokers().get(0).getValue());
            String maxValue = getPokerDisplayValue(pokerInfo.getValue());
            switch (pokerInfo.getPokerModel()) {
                case ROCKET:
                    return isHard ? "Rocket" : "王炸";
                case BOMB:
                    return isHard ? "Bomb" : "炸弹";
                case SINGLE:
                    return isHard ? "Single" : "单张" + minValue;
                case PAIR:
                    return isHard ? "Pair" : "对" + minValue;
                case THREE:
                    return isHard ? "Three" : "三张" + minValue;
                case THREE_ONE_SINGLE:
                    return isHard ? "Three Belt One" : "三带一";
                case THREE_ONE_PAIR:
                    return isHard ? "Three Belt One Pair" : "三带一对";
                case SHUN_ZI_SINGLE:
                    return isHard ? "Single Link" : "单顺子(" + minValue + "~" + maxValue + ")";
                case SHUN_ZI_PAIR:
                    return isHard ? "Pair Link" : "双顺子(" + minValue + "~" + maxValue + ")";
                case PLAIN_UNMANNED:
                    return isHard ? "Plain Unmanned" : "无人飞机";
                case PLAIN_MANNED:
                    return isHard ? "Plain Manned" : "载人飞机";
                case FOUR_TWO_SINGLE:
                    return isHard ? "Four Belt Two Single" : "四带二";
                case FOUR_TWO_PAIR:
                    return isHard ? "Four Belt Two Pair" : "四带俩对";
            }
        }

        return "";
    }

    private String getPokerDisplayValue(int value) {
        switch (value) {
            case 11:
                return "J";
            case 12:
                return "Q";
            case 13:
                return "K";
            case 14:
                return "A";
            case 15:
                return "2";
            case 16:
                return "王";
            case 17:
                return "皇";
            default:
                return value + "";
        }
    }

    private String getPokerDisplayValueForHard(int value) {
        switch (value) {
            case 10:
                return "0x0";
            case 11:
                return "0xB";
            case 12:
                return "0xC";
            case 13:
                return "0xD";
            case 14:
                return "0xA";
            case 15:
                return "0x2";
            case 16:
                return "0xE";
            case 17:
                return "0xF";
            default:
                return "0x" + value;
        }
    }

    private String getPokerDisplaySuits(Poker.Suits suits) {
        if (suits == null || debugMode == DebugMode.SOFT) {
            return "";
        }

        if (isHard()) {
            return "int";
        }

        switch (suits) {
            case SPADE:
                return "♠";
            case HEART:
                return "♥";
            case DIAMOND:
                return "♦";
            case CLUB:
                return "♣";
            default:
                return "";
        }
    }

    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> {
            button.setEnabled(false);
            invoke(() -> {
                isHomeowner = true;
                start();
                button.setEnabled(true);
            }, 100);
        });
        return button;
    }

    private JButton getBackButton() {
        JButton button = new JButton(isHard() ? "Back Debug" : "返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    private void buildPlayerNode() {
        PlayerNode startNode = null;
        PlayerNode playerNode = null;
        playerMap = new HashMap<>();
        List<String> roomUserList = userList;
        int usersTotal = roomUserList.size();

        for (int i = 0; i < usersTotal; i++) {
            PlayerNode node = new PlayerNode();
            node.setPlayer(roomUserList.get(i));
            node.setPokerTotal(17);
            node.setAlias("Machine 0" + (i + 1));
            playerMap.put(node.getPlayer(), new Player(node, null));

            if (GameAction.getNickname().equals(node.getPlayer())) {
                currentPlayer = node;
            }

            if (aiPlayerActionMap.containsKey(node.getPlayer())) {
                aiPlayerActionMap.put(node.getPlayer(), new AIPlayerAction(node));
            }

            if (playerNode == null) {
                playerNode = node;
                startNode = node;
                continue;
            }

            if (i == usersTotal - 1) {
                node.setNextPlayer(startNode);
                startNode.setPrevPlayer(node);
            }

            playerNode.setNextPlayer(node);
            node.setPrevPlayer(playerNode);
            playerNode = node;
        }
    }

    private boolean receivedAndOk() {
        if (receiveCounter.incrementAndGet() == 3) {
            receiveCounter.set(0);
            return true;
        }
        return false;
    }

    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (state > 0 && state < 4) {
            state = 4;
            if (isHard()) {
                String msg = "Debugging is over. The " + playerMap.get(player.getUsername()).getPlayerNode().getAlias() + " has been offline";
                showTips(msg);
            } else {
                showTips("游戏结束！" + player.getUsername() + "逃跑了~");
            }
            gameOverButton.setVisible(true);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum DebugMode {
        DEFAULT("Default"),
        SOFT("Soft"),
        HARD("Hard");

        private String name;

        public static DebugMode getMode(String name) {
            for (DebugMode mode : values()) {
                if (mode.getName().equals(name)) {
                    return mode;
                }
            }

            return null;
        }
    }

    private ComboBox getDebugModelComboBox() {
        ComboBox comboBox = new ComboBox();
        for (DebugMode mode : DebugMode.values()) {
            comboBox.addItem(mode.getName());
            if (debugMode == mode) {
                comboBox.setSelectedItem(mode.getName());
            }
        }
        comboBox.addItemListener(e -> {
            debugMode = DebugMode.getMode(comboBox.getSelectedItem().toString());
            flushDebugMode();
        });
        return comboBox;
    }

    private void flushDebugMode() {
        String title = "斗地主！";
        String backButtonText = "返回游戏";
        String gameOverButtonText = "游戏结束";
        String notOutPokerButtonText = "过！";
        String outPokerButtonText = "出牌";

        if (isHard()) {
            title = "Synergy Debugging";
            backButtonText = "Back Debug";
            gameOverButtonText = "Debug Over";
            notOutPokerButtonText = "Run!";
            outPokerButtonText = "Debug";
        }
        if (debugMode == DebugMode.SOFT) {
            title = "";
        }

        if (backButton != null) {
            backButton.setText(backButtonText);
            backButton.updateUI();
        }
        if (notOutPokerButton != null) {
            notOutPokerButton.setText(notOutPokerButtonText);
            notOutPokerButton.updateUI();
        }
        if (outPokerButton != null) {
            outPokerButton.setText(outPokerButtonText);
            outPokerButton.updateUI();
        }
        titleLabel.setText(title);
        titleLabel.updateUI();
        gameOverButton.setText(gameOverButtonText);
        gameOverButton.updateUI();

        playerMap.forEach((k, v) -> {
            v.flushRole();
            v.flushNickname();
        });
        flushPokers();

        if (lastPokerInfo != null) {
            showPokerInfo(lastPokerInfo);
        }
    }

    public static boolean isHard() {
        return debugMode == DebugMode.HARD;
    }

}
