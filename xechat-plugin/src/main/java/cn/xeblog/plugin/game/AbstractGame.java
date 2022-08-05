package cn.xeblog.plugin.game;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.AbstractPanelComponent;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
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
 * @author anlingyi
 * @date 2020/8/31
 */
public abstract class AbstractGame<T extends GameDTO> extends AbstractPanelComponent implements GameRoomEventHandler {

    private JPanel gameUserPanel;
    private JPanel onlineUserListPanel;
    private JButton startGameButton;
    private JLabel readyLabel;

    private JTextField searchUserField;

    private GameRoomHandler gameRoomHandler;

    public AbstractGame() {
        super(GameAction.isProactive());
        AbstractGame abstractGame = this;
        this.gameRoomHandler = new GameRoomHandler() {
            @Override
            protected void allPlayersGameStarted() {
                abstractGame.allPlayersGameStarted();
            }
        };
    }

    protected final void startGame() {
        start();
        ConsoleAction.gotoConsoleLow(true);
    }

    /**
     * 游戏开始
     */
    protected abstract void start();

    /**
     * 游戏数据处理
     *
     * @param body 数据内容
     */
    public abstract void handle(T body);


    protected void allPlayersGameStarted() {

    }

    /**
     * 获取游戏房间
     *
     * @return
     */
    public GameRoom getRoom() {
        return gameRoomHandler.gameRoom;
    }

    /**
     * 创建游戏房间
     *
     * @param game     游戏
     * @param nums     人数
     * @param gameMode 模式
     */
    private void createRoom(Game game, int nums, String gameMode) {
        gameRoomHandler.createRoom(game, nums, gameMode);
    }

    protected boolean isHomeowner() {
        return gameRoomHandler.isHomeowner;
    }

    protected boolean setHomeowner(boolean isHomeowner) {
        return gameRoomHandler.isHomeowner = isHomeowner;
    }

    /**
     * 发送游戏数据
     *
     * @param body 数据内容
     */
    protected void sendMsg(GameDTO body) {
        body.setRoomId(getRoom().getId());
        MessageAction.send(body, Action.GAME);
    }

    protected JButton getExitButton() {
        JButton exitButton = super.getExitButton();
        exitButton.setText("退出游戏");
        return exitButton;
    }

    protected JButton getCreateRoomButton(List<Integer> numsList) {
        return getCreateRoomButton(numsList, null);
    }

    protected JButton getCreateRoomButton(List<Integer> numsList, List<String> gameModeList) {
        JButton createGameRoomButton = new JButton("创建房间");
        createGameRoomButton.addActionListener(e -> showCreateGameRoomPanel(numsList, gameModeList));
        return createGameRoomButton;
    }

    protected JButton getGameOverButton() {
        JButton exitButton = new JButton("游戏结束");
        exitButton.addActionListener(e -> gameRoomHandler.gameOver());
        return exitButton;
    }

    protected void showCreateGameRoomPanel(List<Integer> numsList, List<String> gameModeList) {
        new CreateGameRoomDialog(numsList, gameModeList).show();
    }

    private static class CreateGameRoomDialog extends DialogWrapper {

        private JPanel main;
        private ComboBox numsBox;
        private List<Integer> numsList;
        private List<String> gameModeList;
        private ComboBox gameModeBox;

        CreateGameRoomDialog(List<Integer> numsList) {
            this(numsList, null);
        }

        CreateGameRoomDialog(List<Integer> numsList, List<String> gameModeList) {
            super(true);
            this.numsList = numsList;
            this.gameModeList = gameModeList;

            setTitle("创建房间");
            setResizable(false);
            setOKActionEnabled(true);
            setOKButtonText("创建");
            setCancelButtonText("取消");
            init();
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            main = new JPanel();
            main.setPreferredSize(new Dimension(200, 50));
            Box vBox = Box.createVerticalBox();
            main.add(vBox);

            Box hBox = Box.createHorizontalBox();
            vBox.add(hBox);

            JLabel label = new JLabel("房间人数：");
            label.setFont(new Font("", 1, 13));
            hBox.add(label);

            numsBox = new ComboBox();
            numsList.forEach(nums -> numsBox.addItem(nums));
            hBox.add(numsBox);

            if (CollUtil.isNotEmpty(gameModeList)) {
                Box hBox2 = Box.createHorizontalBox();
                vBox.add(hBox2);

                JLabel gameModelLabel = new JLabel("游戏模式：");
                gameModelLabel.setFont(new Font("", 1, 13));
                hBox2.add(gameModelLabel);

                gameModeBox = new ComboBox();
                gameModeList.forEach(mode -> gameModeBox.addItem(mode));
                hBox2.add(gameModeBox);
            }

            return main;
        }

        @Override
        protected void doOKAction() {
            int nums = Integer.parseInt(numsBox.getSelectedItem().toString());
            String gameMode = null;
            if (gameModeBox != null) {
                gameMode = gameModeBox.getSelectedItem().toString();
            }
            GameAction.getAction().createRoom(GameAction.getGame(), nums, gameMode);
            super.doOKAction();
        }

    }

    protected void showGameRoomPanel() {
        mainPanel.removeAll();
        mainPanel.setVisible(true);
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(300, 200));

        Box mainVBox = Box.createVerticalBox();
        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 280, 500);

        GameRoom gameRoom = getRoom();
        JLabel titleLabel = new JLabel("欢迎进入【" + gameRoom.getGame().getName() + "】游戏房间~");
        titleLabel.setFont(new Font("", 1, 14));
        titleLabel.setForeground(new Color(239, 106, 106));
        panel.add(titleLabel);

        String gameMode = gameRoom.getGameMode();
        if (gameMode != null) {
            Box hBox = Box.createHorizontalBox();
            JLabel modeLabel = new JLabel(gameMode);
            modeLabel.setFont(new Font("", 1, 13));
            hBox.add(modeLabel);
            mainVBox.add(Box.createVerticalStrut(10));
            mainVBox.add(hBox);
        }

        Box hBox = Box.createHorizontalBox();
        JLabel label1 = new JLabel("【房间玩家】");
        label1.setFont(new Font("", 1, 13));
        hBox.add(label1);

        readyLabel = new JLabel("准备");
        readyLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        readyLabel.setFont(new Font("", 1, 12));
        readyLabel.setForeground(new Color(255, 104, 104));
        readyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (readyLabel.isEnabled()) {
                    gameRoomHandler.playerReady();
                }
            }
        });
        hBox.add(readyLabel);

        Box vBox = Box.createVerticalBox();
        gameUserPanel = new JPanel();
        vBox.add(gameUserPanel);
        flushGameRoomUsers();

        Box hBox2 = Box.createHorizontalBox();
        JLabel label2 = new JLabel("【在线玩家】");
        label2.setFont(new Font("", 1, 13));
        hBox2.add(label2);

        JLabel flushOnlineUserLabel = new JLabel("刷新");
        flushOnlineUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        flushOnlineUserLabel.setFont(new Font("", 1, 12));
        flushOnlineUserLabel.setForeground(new Color(255, 104, 104));
        flushOnlineUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flushOnlineUsers();
            }
        });
        hBox2.add(flushOnlineUserLabel);

        Box vBox2 = Box.createVerticalBox();

        searchUserField = new JTextField();
        searchUserField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                flushOnlineUsers();
            }
        });
        vBox2.add(searchUserField);
        vBox2.add(Box.createVerticalStrut(5));

        JPanel onlineUserMainPanel = new JPanel(new BorderLayout());
        onlineUserMainPanel.setPreferredSize(new Dimension(250, 150));
        onlineUserListPanel = new JPanel();
        flushOnlineUsers();
        JBScrollPane onlineUserScrollBar = new JBScrollPane(onlineUserListPanel);
        onlineUserScrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        onlineUserMainPanel.add(onlineUserScrollBar);
        vBox2.add(onlineUserMainPanel);

        Box hBox3 = Box.createHorizontalBox();
        if (isHomeowner()) {
            startGameButton = new JButton("开始游戏");
            startGameButton.setEnabled(false);
            startGameButton.addActionListener(e -> {
                gameRoomHandler.gameStart();
                startGameButton.setEnabled(false);
            });
            hBox3.add(startGameButton);

            JButton closeRoomButton = new JButton("关闭房间");
            closeRoomButton.addActionListener(e -> {
                gameRoomHandler.closeRoom();
                init();
            });
            hBox3.add(closeRoomButton);
        } else {
            hBox3.add(getExitButton());
        }

        mainVBox.add(Box.createVerticalStrut(20));
        mainVBox.add(hBox);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(vBox);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(hBox3);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(hBox2);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(vBox2);
        panel.add(mainVBox);

        mainPanel.add(panel);
        mainPanel.updateUI();
    }

    private int getUserStatusOrder(UserStatus status) {
        switch (status) {
            case FISHING:
                return 0;
            case PLAYING:
                return 1;
            case WORKING:
                return 2;
        }

        return -1;
    }

    private void flushOnlineUsers() {
        GameRoom gameRoom = getRoom();
        if (gameRoom == null) {
            return;
        }

        Map<String, User> onlineUserMap = DataCache.userMap;
        List<User> userList = new ArrayList<>();
        String search = searchUserField != null ? searchUserField.getText() : "";
        if (StrUtil.isBlank(search)) {
            userList.addAll(onlineUserMap.values());
        } else {
            onlineUserMap.forEach((k, v) -> {
                if (k.toLowerCase().contains(search.toLowerCase())) {
                    userList.add(v);
                }
            });
        }

        userList.sort((u1, u2) -> {
            int o1 = getUserStatusOrder(u1.getStatus());
            int o2 = getUserStatusOrder(u2.getStatus());
            if (o1 < o2) {
                return -1;
            }
            if (o1 == o2) {
                return 0;
            }
            return 1;
        });

        onlineUserListPanel.removeAll();
        Box vBox = Box.createVerticalBox();
        for (User user : userList) {
            if (user.getUsername().equals(GameAction.getNickname())) {
                continue;
            }

            JPanel userPanel = new JPanel();
            JLabel label = new JLabel(user.getUsername() + "(" + user.getStatus().alias() + ")");
            label.setFont(new Font("", 0, 13));
            userPanel.add(label);
            if (isHomeowner() && user.getStatus() == UserStatus.FISHING && !gameRoom.isOvered()) {
                JLabel inviteLabel = new JLabel("邀请");
                inviteLabel.setFont(new Font("", 1, 12));
                inviteLabel.setForeground(new Color(93, 187, 70));
                inviteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                inviteLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (inviteLabel.isEnabled()) {
                            gameRoomHandler.invitePlayer(user.getUsername());
                            inviteLabel.setEnabled(false);
                            inviteLabel.setText("已邀请");
                        }
                    }
                });
                userPanel.add(inviteLabel);
            }
            vBox.add(userPanel);
        }

        onlineUserListPanel.add(vBox);
        onlineUserListPanel.updateUI();
    }

    private void flushGameRoomUsers() {
        GameRoom gameRoom = getRoom();
        if (gameRoom == null) {
            return;
        }

        int nums = gameRoom.getNums();
        List<GameRoom.Player> userList = new ArrayList<>(gameRoom.getUsers().values());
        int currentNums = userList.size();
        int readyCount = 0;
        Box vBox = Box.createVerticalBox();
        gameUserPanel.removeAll();
        for (int i = 0; i < nums; i++) {
            Color color = new Color(69, 232, 232);
            GameRoom.Player player = i >= currentNums ? null : userList.get(i);
            String username = null;
            if (player != null) {
                username = player.getUsername();
                if (gameRoom.isHomeowner(player.getUsername())) {
                    username += " [房主]";
                }
                if (player.isReadied()) {
                    readyCount++;
                    username += " (OK)";
                    color = new Color(107, 215, 80);
                }
                username = (i + 1) + ". " + username;
            }

            if (username == null) {
                color = new Color(171, 170, 170);
                username = "[虚位以待]";
            }
            JLabel usernameLabel = new JLabel(username);
            usernameLabel.setForeground(color);
            usernameLabel.setFont(new Font("", 0, 13));
            vBox.add(usernameLabel);
            vBox.add(Box.createVerticalStrut(5));
        }

        if (startGameButton != null) {
            startGameButton.setEnabled(readyCount == nums);
        }

        gameUserPanel.add(vBox);
        gameUserPanel.updateUI();
    }

    @Override
    public void roomCreated(GameRoom gameRoom) {
        gameRoomHandler.roomCreated(gameRoom);
        roomOpened(gameRoom);
    }

    @Override
    public void roomOpened(GameRoom gameRoom) {
        gameRoomHandler.roomOpened(gameRoom);
        invoke(() -> showGameRoomPanel());
    }

    @Override
    public void roomClosed() {
        gameRoomHandler.roomClosed();
        if (isHomeowner()) {
            GameAction.setRoomId(null);
            invoke(() -> init());
        } else {
            GameAction.over();
        }
    }

    @Override
    public void playerJoined(User player) {
        gameRoomHandler.playerJoined(player);
        invoke(() -> {
            flushGameRoomUsers();
            flushOnlineUsers();
        });
    }

    @Override
    public void playerLeft(User player) {
        gameRoomHandler.playerLeft(player);
        invoke(() -> {
            flushGameRoomUsers();
            flushOnlineUsers();
        });
    }

    @Override
    public void playerInviteFailed(User player) {
        gameRoomHandler.playerInviteFailed(player);
        invoke(() -> flushOnlineUsers());
    }

    @Override
    public void playerReadied(User player) {
        gameRoomHandler.playerReadied(player);
        invoke(() -> {
            if (player.getUsername().equals(GameAction.getNickname())) {
                readyLabel.setEnabled(false);
                readyLabel.setText("已准备");
            }
            flushGameRoomUsers();
        });
    }

    @Override
    public void gameStarted(GameRoom gameRoom) {
        gameRoomHandler.gameStarted(gameRoom);
        invoke(() -> {
            startGame();
            gameRoomHandler.playerGameStarted();
        });
    }

    @Override
    public void gameEnded() {
        gameRoomHandler.gameEnded();
        invoke(() -> showGameRoomPanel());
    }

    @Override
    public void playerGameStarted(User user) {
        gameRoomHandler.playerGameStarted(user);
    }

}
