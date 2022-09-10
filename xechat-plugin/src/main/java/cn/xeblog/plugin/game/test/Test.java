package cn.xeblog.plugin.game.test;

import cn.xeblog.commons.entity.game.gobang.GobangDTO;
import cn.xeblog.commons.entity.game.test.TestDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DoGame(Game.TEST)
public class Test extends AbstractGame<TestDTO> {

    @Override
    protected void start() {
        {
            mainPanel.removeAll();
            mainPanel.setLayout(new BorderLayout());
            mainPanel.setMinimumSize(new Dimension(150, 150));
        }

        JPanel gamePanel = new JPanel();
        Box vBox = Box.createVerticalBox();
        JLabel label = new JLabel("联机游戏已经开始！");
        vBox.add(label);

        vBox.add(Box.createVerticalStrut(10));
        JLabel label2 = new JLabel("玩家列表");
        gamePanel.add(label2);
        vBox.add(label2);

        vBox.add(Box.createVerticalStrut(10));
        getRoom().getUsers().forEach((k, v) -> {
            JLabel playerLabel = new JLabel(k);
            gamePanel.add(playerLabel);
            vBox.add(playerLabel);
        });

        vBox.add(Box.createVerticalStrut(10));
        JButton sendDataButton = new JButton("发送游戏数据");
        sendDataButton.addActionListener(e -> {
            TestDTO dto = new TestDTO();
            dto.setX(1);
            dto.setType(666);
            sendMsg(dto);
        });
        vBox.add(sendDataButton);

        gamePanel.add(vBox);

        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(getGameOverButton(), BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    @Override
    protected void init() {
        {
            mainPanel.removeAll();
            mainPanel.setLayout(null);
            mainPanel.setEnabled(true);
            mainPanel.setVisible(true);
            mainPanel.setMinimumSize(new Dimension(150, 200));
        }
        JPanel startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("测试游戏！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);
        vBox.add(Box.createVerticalStrut(20));

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> {
            startGame0();
        });
        vBox.add(startGameButton);

        if (DataCache.isOnline) {
            // 如果已经是登录状态，就显示创建房间按钮
            List<Integer> numsList = new ArrayList<>();
            numsList.add(2);
            numsList.add(3);
            numsList.add(4);
            vBox.add(getCreateRoomButton(numsList));
        }

        vBox.add(getExitButton());
        mainPanel.updateUI();
    }

    private void startGame0() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(150, 200));

        JLabel label = new JLabel("游戏已经开始！");
        mainPanel.add(label, BorderLayout.CENTER);

        JButton backButton = new JButton("返回游戏");
        backButton.addActionListener(e -> init());
        mainPanel.add(backButton, BorderLayout.SOUTH);
        mainPanel.updateUI();
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
                handle(new TestDTO(0, 0, randomType));
            }, 500);
        }
    }

    @Override
    public void handle(TestDTO body) {
        System.out.println("收到游戏数据：" + body);
    }
}