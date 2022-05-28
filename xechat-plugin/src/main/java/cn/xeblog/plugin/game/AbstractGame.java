package cn.xeblog.plugin.game;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.ui.MainWindow;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * @author anlingyi
 * @date 2020/8/31
 */
public abstract class AbstractGame<T extends GameDTO> extends GameRoomHandler {

    protected final JPanel mainPanel;

    private JPanel gameUserPanel;
    private JPanel onlineUserPanel;
    private JButton startGameButton;

    public AbstractGame() {
        this.mainPanel = MainWindow.getInstance().getRightPanel();
        if (GameAction.isProactive()) {
            init();
        }
    }

    protected abstract void init();

    protected abstract void start();

    public abstract void handle(T body);

    protected void sendMsg(GameDTO body) {
        body.setRoomId(gameRoom.getId());
        MessageAction.send(body, Action.GAME);
    }

    public void over() {
        invoke(() -> {
            mainPanel.setVisible(false);
            mainPanel.removeAll();
            mainPanel.updateUI();
        });
    }

    protected JButton getExitButton() {
        JButton exitButton = new JButton("退出游戏");
        exitButton.addActionListener(e -> Command.GAME_OVER.exec(null));
        return exitButton;
    }

    protected JButton getCreateRoomButton(List<Integer> numsList) {
        JButton createGameRoomButton = new JButton("创建房间");
        createGameRoomButton.addActionListener(e -> showCreateGameRoomPanel(numsList));
        return createGameRoomButton;
    }

    protected JButton getGameOverButton() {
        JButton exitButton = new JButton("游戏结束");
        exitButton.addActionListener(e -> gameOver());
        return exitButton;
    }

    protected void showCreateGameRoomPanel(List<Integer> numsList) {
        new CreateGameRoomDialog(numsList).show();
    }

    private static class CreateGameRoomDialog extends DialogWrapper {

        private JPanel main;
        private ComboBox numsBox;
        private List<Integer> numsList;

        CreateGameRoomDialog(List<Integer> numsList) {
            super(true);
            this.numsList = numsList;

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
            JLabel label = new JLabel("房间人数：");
            label.setFont(new Font("", 1, 13));
            main.add(label);

            numsBox = new ComboBox();
            numsList.forEach(nums -> numsBox.addItem(nums));
            main.add(numsBox);

            return main;
        }

        @Override
        protected void doOKAction() {
            int nums = Integer.parseInt(numsBox.getSelectedItem().toString());
            GameAction.getAction().createRoom(GameAction.getGame(), nums);
            super.doOKAction();
        }

    }

    protected void showGameRoomPanel() {
        mainPanel.removeAll();
        mainPanel.setVisible(true);
        mainPanel.setLayout(null);
        mainPanel.setMinimumSize(new Dimension(300, 400));

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 280, 400);

        Box hBox = Box.createHorizontalBox();
        JLabel label1 = new JLabel("[房间玩家]");
        label1.setFont(new Font("", 1, 13));
        hBox.add(label1);

        Box vBox = Box.createVerticalBox();
        gameUserPanel = new JPanel();
        vBox.add(gameUserPanel);
        flushGameRoomUsers();

        Box hBox2 = Box.createHorizontalBox();
        JLabel label2 = new JLabel("[在线玩家]");
        label2.setFont(new Font("", 1, 13));
        hBox2.add(label2);

        JLabel flushOnlineUserLabel = new JLabel("刷新");
        flushOnlineUserLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        flushOnlineUserLabel.setFont(new Font("", 1, 12));
        flushOnlineUserLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flushOnlineUsers();
            }
        });
        hBox2.add(Box.createHorizontalStrut(10));
        hBox2.add(flushOnlineUserLabel);

        Box vBox2 = Box.createVerticalBox();
        onlineUserPanel = new JPanel();
        vBox2.add(onlineUserPanel);
        flushOnlineUsers();

        Box hBox3 = Box.createHorizontalBox();
        if (gameRoom.getHomeowner().equals(DataCache.username)) {
            startGameButton = new JButton("开始游戏");
            startGameButton.setEnabled(false);
            startGameButton.addActionListener(e -> {
                gameStart();
                startGameButton.setEnabled(false);
            });
            hBox3.add(startGameButton);

            JButton closeRoomButton = new JButton("关闭房间");
            closeRoomButton.addActionListener(e -> {
                closeRoom();
                init();
            });
            hBox3.add(closeRoomButton);
        } else {
            hBox3.add(getExitButton());
        }

        Box mainVBox = Box.createVerticalBox();
        mainVBox.add(hBox);
        mainVBox.add(vBox);
        mainVBox.add(hBox2);
        mainVBox.add(vBox2);
        mainVBox.add(hBox3);
        panel.add(mainVBox);

        mainPanel.add(panel);
        mainPanel.updateUI();
    }

    private void flushOnlineUsers() {
        Map<String, User> onlineUserMap = DataCache.userMap;
        List<User> userList = new ArrayList<>(onlineUserMap.values());
        userList.sort((u1, u2) -> {
            if (u1.getStatus() == UserStatus.FISHING) {
                return -1;
            }
            return 1;
        });

        onlineUserPanel.removeAll();
        userList.forEach(user -> {
            if (user.getUsername().equals(DataCache.username)) {
                return;
            }

            JPanel userPanel = new JPanel();
            JLabel label = new JLabel(user.getUsername() + "(" + user.getStatus().alias() + ")");
            label.setFont(new Font("", 0, 13));
            userPanel.add(label);
            if (user.getStatus() == UserStatus.FISHING) {
                JLabel inviteLabel = new JLabel("邀请");
                inviteLabel.setFont(new Font("", 1, 12));
                inviteLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                inviteLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        invitePlayer(user.getUsername());
                        inviteLabel.setEnabled(false);
                        inviteLabel.setText("已邀请");
                    }
                });
                userPanel.add(inviteLabel);
            }
            onlineUserPanel.add(userPanel);
        });
        onlineUserPanel.updateUI();
    }

    private void flushGameRoomUsers() {
        int nums = gameRoom.getNums();
        List<GameRoom.Player> userList = new ArrayList<>(gameRoom.getUsers().values());
        int currentNums = userList.size();
        gameUserPanel.removeAll();
        int readyCount = 0;
        Box hBox = Box.createHorizontalBox();
        for (int i = 0; i < nums; i++) {
            GameRoom.Player player = i >= currentNums ? null : userList.get(i);
            String username = null;
            if (player != null) {
                username = player.getUsername();
                if (player.isReadied()) {
                    readyCount++;
                    username += "(已准备)";
                }
            }

            Box vBox = Box.createVerticalBox();
            vBox.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel usernameLabel = new JLabel(username == null ? "空位" : username);
            usernameLabel.setFont(new Font("", 0, 13));
            vBox.add(usernameLabel);
            if (player != null && !player.isReadied() && DataCache.username.equals(username)) {
                JLabel readyLabel = new JLabel("准备");
                readyLabel.setFont(new Font("", 1, 12));
                readyLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                readyLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        readyLabel.setEnabled(false);
                        playerReady();
                    }
                });
                vBox.add(readyLabel);
            }
            hBox.add(vBox);
            hBox.add(Box.createHorizontalStrut(10));
        }

        if (startGameButton != null) {
            startGameButton.setEnabled(readyCount == nums);
        }

        gameUserPanel.add(hBox);
        gameUserPanel.updateUI();
    }

    @Override
    public void roomCreated(GameRoom gameRoom) {
        super.roomCreated(gameRoom);
        invoke(() -> showGameRoomPanel());
    }

    @Override
    public void roomOpened(GameRoom gameRoom) {
        super.roomOpened(gameRoom);
        this.gameRoom = gameRoom;
        showGameRoomPanel();
    }

    @Override
    public void roomClosed() {
        super.roomClosed();
        if (isHomeowner) {
            GameAction.setRoomId(null);
            invoke(() -> init());
        } else {
            GameAction.over();
        }
    }

    @Override
    public void playerJoined(User player) {
        super.playerJoined(player);
        invoke(() -> {
            flushGameRoomUsers();
            flushOnlineUsers();
        });
    }

    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (gameRoom.getHomeowner().equals(player.getUsername())) {
            GameAction.over();
        } else {
            invoke(() -> {
                flushGameRoomUsers();
                flushOnlineUsers();
            });
        }
    }

    @Override
    public void playerInviteFailed(User player) {
        super.playerInviteFailed(player);
        invoke(() -> flushOnlineUsers());
    }

    @Override
    public void playerReadied(User player) {
        super.playerReadied(player);
        invoke(() -> flushGameRoomUsers());
    }

    @Override
    public void gameStarted(GameRoom gameRoom) {
        super.gameStarted(gameRoom);
        invoke(() -> start());
    }

    @Override
    public void gameEnded() {
        super.gameEnded();
        invoke(() -> showGameRoomPanel());
    }

    protected final void invoke(Runnable runnable) {
        ApplicationManager.getApplication().invokeLater(runnable);
    }

}
