package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.game.chess.ChessDTO;
import cn.xeblog.commons.entity.game.gobang.GobangDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DoGame(Game.CHINESE_CHESS)
public class ChineseChess extends AbstractGame<ChessDTO> {

    @Override
    protected void start() {
        startGame0();
    }

    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 200));
        JPanel startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("测试游戏！");
        title.setFont(new Font("", Font.BOLD, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> startGame0());
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

        vBox.add(getExitButton());

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
                handle(new ChessDTO(0, 0, randomType));
            }, 500);
        }
    }

    @Override
    public void handle(ChessDTO body) {

        System.out.println(body.toString());

    }

    private void startGame0() {
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(466,460));

        /*JButton backButton = new JButton("返回游戏");
        backButton.addActionListener(e -> init());
        mainPanel.add(backButton, BorderLayout.SOUTH);*/

        // 游戏面板
        mainPanel.add(new GamePanel());

        mainPanel.updateUI();
    }

    public void send(Point point) {
        GobangDTO dto = new GobangDTO();
        dto.setX(point.x);
        dto.setY(point.y);
        dto.setType(point.type);
        sendMsg(dto);
    }
}
