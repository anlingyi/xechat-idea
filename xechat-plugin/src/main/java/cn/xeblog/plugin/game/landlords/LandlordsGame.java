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
import com.intellij.ui.components.JBScrollPane;
import lombok.AllArgsConstructor;
import lombok.Data;
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

    private JPanel startPanel;
    private JButton gameOverButton;
    private JButton outPokerButton;
    private JButton notOutPokerButton;
    private JLabel tipsLabel;
    private JPanel outPokerPanel;
    private JPanel playerTopPanel;

    private Map<String, Player> playerMap;

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
        playerMap = new HashMap<>();
        selectedPokers = new ArrayList<>();
        receiveCounter = new AtomicInteger();
    }

    @Override
    protected void init() {
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
//        vBox.add(getStartGameButton());
        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            numsList.add(3);
            vBox.add(getCreateRoomButton(numsList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    protected void start() {
        initValue();

        buildPlayerNode();
        showGamePanel();
        state = 1;
        showTips("请等待...");

        if (isHomeowner) {
            spinMoment(100);
            allocPokersMsg();
        }
    }

    private void allocPokersMsg() {
        int priority = RandomUtil.randomInt(3);
        List<List<Poker>> allocPokers = PokerUtil.allocPokers();
        List<String> playerList = new ArrayList<>(gameRoom.getUsers().keySet());
        for (int i = 0; i < playerList.size(); i++) {
            sendMsg(LandlordsGameDTO.MsgType.ALLOC_POKER, playerList.get(i),
                    new AllocPokerDTO(allocPokers.get(i), allocPokers.get(3), priority == i));
        }
    }

    private void sendMsg(LandlordsGameDTO.MsgType msgType, Object data) {
        sendMsg(msgType, currentPlayer.getPlayer(), data);
    }

    private void sendMsg(LandlordsGameDTO.MsgType msgType, String player, Object data) {
        LandlordsGameDTO dto = new LandlordsGameDTO();
        dto.setMsgType(msgType);
        dto.setPlayer(player);
        dto.setData(data);
        sendMsg(dto);
        handle(dto);
    }

    @Override
    public void handle(LandlordsGameDTO body) {
        Player player = playerMap.get(body.getPlayer());
        PlayerNode playerNode = player.getPlayerNode();
        boolean isMe = playerNode == currentPlayer;
        switch (body.getMsgType()) {
            case ALLOC_POKER:
                if (receivedAndOk()) {
                    state = 2;
                }

                AllocPokerDTO allocPokerDTO = (AllocPokerDTO) body.getData();
                List<Poker> allocPokerList = allocPokerDTO.getPokers();
                playerNode.setPokerTotal(allocPokerList.size());
                if (isMe) {
                    pokers = allocPokerList;
                    lastPokers = allocPokerDTO.getLastPokers();
                    showTips("正在确定地主...");
                }
                flushPlayerPanel(player);

                if (allocPokerDTO.isPrioritized()) {
                    if (isMe) {
                        spinMoment(100);
                        showCallScoreButton(0);
                    } else {
                        player.showTips("优先叫地主！");
                    }
                }
                break;
            case CALL_SCORE:
                Player nextPlayer = playerMap.get(playerNode.getNextPlayer().getPlayer());
                String tips;
                int score = (int) body.getData();
                if (score == 0) {
                    tips = "不叫！";
                } else {
                    tips = score + "分！";
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
                    showTips("未确定地主！正在重新发牌...");
                    if (isHomeowner) {
                        invoke(() -> {
                            spinMoment(2000);
                            allocPokersMsg();
                        });
                    }
                    break;
                }

                if (!isOk && score == 3 || isOk && maxScorePlayer != null) {
                    state = 3;

                    PlayerNode maxScorePlayerNode = maxScorePlayer.getPlayerNode();
                    maxScorePlayerNode.setRole(2);
                    maxScorePlayerNode.setPokerTotal(playerNode.getPokerTotal() + 3);

                    showTips(maxScorePlayerNode.getPlayer() + (maxScorePlayerNode == currentPlayer ? "(你)" : "") + "成为地主！");
                    showPokers(lastPokers);

                    PlayerNode prevPlayerNode = maxScorePlayerNode.getPrevPlayer();
                    PlayerNode nextPlayerNode = maxScorePlayerNode.getNextPlayer();
                    prevPlayerNode.setRole(1);
                    nextPlayerNode.setRole(1);

                    Player masterPrevPlayer = playerMap.get(prevPlayerNode.getPlayer());
                    Player masterNextPlayer = playerMap.get(nextPlayerNode.getPlayer());
                    masterPrevPlayer.flushRole();
                    masterNextPlayer.flushRole();
                    if (prevPlayerNode != currentPlayer) {
                        masterPrevPlayer.showTips("");
                    }
                    if (nextPlayerNode != currentPlayer) {
                        masterNextPlayer.showTips("");
                    }

                    if (maxScorePlayerNode == currentPlayer) {
                        pokers.addAll(lastPokers);
                        PokerUtil.sorted(pokers, true);
                        flushPlayerPanel(player);
                        spinMoment(100);
                        showOutPokerButton(true);
                    } else {
                        maxScorePlayer.showTips("思考中...");
                        playerMap.get(maxScorePlayerNode.getPlayer()).flushRole();
                    }
                } else {
                    if (nextPlayer.getPlayerNode() == currentPlayer) {
                        showCallScoreButton(maxScore);
                    } else {
                        nextPlayer.showTips("叫分中...");
                    }
                }
                break;
            case OUT_POKER:
                PokerInfo outPokerInfo = (PokerInfo) body.getData();
                if (outPokerInfo == null) {
                    String tips2 = "过！";
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
                    showTips("[" + lastPlayerRole + "]" + lastPlayer + (isMe ? "(你)" : "") + "已出牌");
                    showPokerInfo(lastPokerInfo);
                    player.flushRole();
                    if (isOver) {
                        showGameOver(playerNode.getRole());
                        return;
                    }
                }

                PlayerNode nextPlayerNode = playerNode.getNextPlayer();
                if (nextPlayerNode == currentPlayer) {
                    showOutPokerButton(false);
                } else {
                    playerMap.get(nextPlayerNode.getPlayer()).showTips("思考中...");
                }
                break;
        }
    }

    private void showGameOver(int role) {
        String roleName = role == 1 ? "农民" : "地主";
        state = role == currentPlayer.getRole() ? 5 : 4;
        showTips(roleName + "胜利！");
        gameOverButton.setVisible(true);
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

        playerMap.get(currentPlayer.getPrevPlayer().getPlayer()).setPanel(leftPanel);
        playerMap.get(currentPlayer.getPlayer()).setPanel(bottomPanel);
        playerMap.get(currentPlayer.getNextPlayer().getPlayer()).setPanel(rightPanel);
        playerMap.forEach((k, v) -> flushPlayerPanel(v));

        JPanel mainTopPanel = new JPanel();
        JLabel title = new JLabel("斗地主！");
        title.setFont(new Font("", 1, 14));
        mainTopPanel.add(title);

        JPanel mainBottomPanel = new JPanel();
        if (gameRoom == null) {
            mainBottomPanel.add(getBackButton());
        }
        gameOverButton = getGameOverButton();
        gameOverButton.setVisible(false);
        mainBottomPanel.add(gameOverButton);

        mainPanel.add(mainTopPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
        JPanel placeholderPanel = new JPanel();
        placeholderPanel.setMinimumSize(new Dimension(5, 0));
        mainPanel.add(placeholderPanel, BorderLayout.WEST);
        mainPanel.add(placeholderPanel, BorderLayout.EAST);
        mainPanel.updateUI();
    }

    private Border pokerBorder = BorderFactory.createLineBorder(new Color(0, 0, 0));
    private Border pokerSelectedBorder = BorderFactory.createLineBorder(new Color(255, 104, 104));
    private Color playerTipsColor = new Color(241, 135, 135);

    private void flushPlayerPanel(Player player) {
        PlayerNode playerNode = player.getPlayerNode();
        JPanel panel = player.getPanel();
        panel.removeAll();

        JLabel nicknameLabel = new JLabel("<html>" + playerNode.getPlayer() + "</html>");
        JLabel roleLabel = new JLabel();
        JPanel mainPanel = new JPanel();

        if (playerNode == currentPlayer) {
            mainPanel.setPreferredSize(new Dimension(460, 150));

            playerTopPanel = new JPanel();
            playerTopPanel.setPreferredSize(new Dimension(460, 40));
            panel.add(playerTopPanel);

            JPanel pokerMainPanel = new JPanel(new BorderLayout());
            pokerMainPanel.setPreferredSize(new Dimension(460, 70));
            JPanel pokerListPanel = new JPanel();
            if (pokers != null) {
                pokers.forEach(poker -> {
                    JPanel pokerPanel = getPokerPanel(poker);
                    pokerListPanel.add(pokerPanel);

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
            }

            JBScrollPane pokersScroll = new JBScrollPane(pokerListPanel);
            pokersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            pokersScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            pokerMainPanel.add(pokersScroll);
            mainPanel.add(pokerMainPanel);
            nicknameLabel.setText(playerNode.getPlayer());
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

        player.setRoleLabel(roleLabel);
        player.flushRole();

        panel.add(mainPanel);
        panel.updateUI();
    }

    private void showOutPokerButton(boolean started) {
        boolean lastIsMe = currentPlayer.getPlayer().equals(lastPlayer);
        if (lastIsMe) {
            lastPokerInfo = null;
        }

        playerTopPanel.removeAll();
        outPokerButton = new JButton("出牌");
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
            notOutPokerButton = new JButton("过！");
            notOutPokerButton.addActionListener(e -> {
                sendMsg(LandlordsGameDTO.MsgType.OUT_POKER, null);
            });

            playerTopPanel.add(notOutPokerButton);
        }

        playerTopPanel.add(outPokerButton);
        playerTopPanel.updateUI();
    }

    private void showCallScoreButton(int score) {
        playerTopPanel.removeAll();
        JButton notCallScoreButton = new JButton("不叫");
        notCallScoreButton.addActionListener(e -> {
            if (state != 2) {
                return;
            }
            sendMsg(LandlordsGameDTO.MsgType.CALL_SCORE, 0);
        });
        playerTopPanel.add(notCallScoreButton);

        for (int i = score + 1; i < 4; i++) {
            int callScore = i;
            JButton callScoreButton = new JButton(callScore + "分");
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
        JPanel pokerPanel = new JPanel();
        pokerPanel.setBorder(pokerBorder);
        pokerPanel.setBackground(new Color(217, 214, 214));
        pokerPanel.setPreferredSize(new Dimension(30, 55));
        pokerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        int value = poker.getValue();
        Poker.Suits suits = poker.getSuits();
        Color pokerColor;
        if (suits == Poker.Suits.SPADE || suits == Poker.Suits.CLUB || value == 16) {
            pokerColor = new Color(0, 0, 0);
        } else {
            pokerColor = new Color(252, 78, 78);
        }

        JLabel pokerValue = new JLabel(" " + getPokerDisplayValue(value));
        pokerValue.setForeground(pokerColor);
        pokerValue.setPreferredSize(new Dimension(30, 12));

        JLabel pokerSuits = new JLabel(" " + getPokerDisplaySuits(suits));
        pokerSuits.setForeground(pokerColor);
        pokerSuits.setPreferredSize(new Dimension(30, 10));

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
        if (pokerInfo != null) {
            String minValue = getPokerDisplayValue(pokerInfo.getPokers().get(0).getValue());
            String maxValue = getPokerDisplayValue(pokerInfo.getValue());
            switch (pokerInfo.getPokerModel()) {
                case ROCKET:
                    return "王炸";
                case BOMB:
                    return "炸弹";
                case SINGLE:
                    return "单张" + minValue;
                case PAIR:
                    return "对" + minValue;
                case THREE:
                    return "三张" + minValue;
                case THREE_ONE_SINGLE:
                    return "三带一";
                case THREE_ONE_PAIR:
                    return "三带一对";
                case SHUN_ZI_SINGLE:
                    return "单顺子(" + minValue + "~" + maxValue + ")";
                case SHUN_ZI_PAIR:
                    return "双顺子(" + minValue + "~" + maxValue + ")";
                case PLAIN_UNMANNED:
                    return "无人飞机";
                case PLAIN_MANNED:
                    return "载人飞机";
                case FOUR_TWO_SINGLE:
                    return "四带二";
                case FOUR_TWO_PAIR:
                    return "四带俩对";
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

    private String getPokerDisplaySuits(Poker.Suits suits) {
        if (suits == null) {
            return "";
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
        button.addActionListener(e -> start());
        return button;
    }

    private JButton getBackButton() {
        JButton button = new JButton("返回游戏");
        button.addActionListener(e -> init());
        return button;
    }

    private void buildPlayerNode() {
        PlayerNode startNode = null;
        PlayerNode playerNode = null;
        List<String> roomUserList = new ArrayList<>(gameRoom.getUsers().keySet());
        int usersTotal = roomUserList.size();
        for (int i = 0; i < usersTotal; i++) {
            PlayerNode node = new PlayerNode();
            node.setPlayer(roomUserList.get(i));
            node.setPokerTotal(17);
            playerMap.put(node.getPlayer(), new Player(node, null));

            if (GameAction.getNickname().equals(node.getPlayer())) {
                currentPlayer = node;
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
            showTips("游戏结束！" + player.getUsername() + "逃跑了~");
            gameOverButton.setVisible(true);
        }
    }

}
