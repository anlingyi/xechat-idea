package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.game.chess.ChessDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.gobang.Gobang;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@DoGame(Game.CHINESE_CHESS)
public class ChineseChess extends AbstractGame<ChessDTO> {

    // 开始界面
    private JPanel startPanel;

    // 开始界面
    private GamePanel gamePanel;

    // 开始界面
    private JComboBox<String> jcb_fightType, jcb_playFirst;

    /** 对战方式（0-人机对战，1-人人对战） */
    int fightType;

    /** 先手选择（1-我方先手，2-对方先手） */
    int playFirst;

    // 游戏状态 -2.初始化 -1.待开始 0.进行中 1.赢 2.平
     int status = -2;

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
        mainPanel.setMinimumSize(new Dimension(150, 320));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 320);

        startPanel();

        mainPanel.add(startPanel);

        mainPanel.updateUI();
    }

    @Override
    protected void allPlayersGameStarted() {
        if (isHomeowner()) {
            // 自旋等待一段时间，再发送游戏数据
            invoke(() -> {
                int randomType = new Random().nextInt(2) + 1;
                ChessDTO msg = new ChessDTO();
                msg.setType(3 - randomType);
                sendMsg(msg);
                handle(new ChessDTO(0, 0, randomType, this.playFirst));
            }, 500);
        }
    }

    @Override
    public void handle(ChessDTO body) {
        if (status > -1) {
            gamePanel.setChess(new Point(body.getX(), body.getY(), body.getType()));

            if (gamePanel.getNextChessColor() == this.gamePanel.BLACKCHESS) {
//                changePlayer();
            }

            checkStatus("nextPlayer");
            if (gamePanel.isGameOver) {
                return;
            }
        } else {
            status = 0;
            /*type = body.getType();
            if (type == 2) {
                put = true;
                changePlayer();
                showTips(player + (GameAction.getNickname().equals(player) ? "(你)" : "") + "先下手为强！");
                return;
            }*/
        }

        /*put = false;
        showTips(player + "(你)：思考中...");*/
    }

    private void startGame0() {
        status = -1;
        if (isHomeowner()) {
            //对战方式
            if("人人对战".equals(jcb_fightType.getSelectedItem().toString()))
            {
                this.fightType = 1;
            }
            else
            {
                this.fightType = 0;
            }

            //先手选择
            if("对方先手".equals(jcb_playFirst.getSelectedItem().toString()))
            {
                this.playFirst = 2;
            }
            else
            {
                this.playFirst = 1;
            }
            ChessDTO msg = new ChessDTO();
            msg.setType(fightType);
            msg.setPlayFirst(playFirst);
            sendMsg(msg);
        }
        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(320,450));

        /*JButton backButton = new JButton("返回游戏");
        backButton.addActionListener(e -> init());
        mainPanel.add(backButton, BorderLayout.SOUTH);*/
        this.gamePanel = new GamePanel(this);
        // 游戏面板
        mainPanel.add(gamePanel);

        mainPanel.updateUI();
    }

    public void send(Point point) {
        ChessDTO dto = new ChessDTO();
        dto.setX(point.x);
        dto.setY(point.y);
        dto.setType(point.type);
        sendMsg(dto);
    }

    /**
     * 功能：右边功能区布局<br>
     */
    private void startPanel()
    {
        //logo图片
        ImageIcon imageIconLogo = new ImageIcon(new ImageIcon(this.getClass().getResource("/images/chess/logo.png")).getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH));//缩放图片来适应标签大小
        JLabel labelLogo = new JLabel(imageIconLogo);
        startPanel.add(labelLogo);
        //对战方式
        JLabel jlb_fightType = new JLabel("对战方式：");
        jlb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jlb_fightType);
        // TODO 现阶段只搞人人对战（人机有点bug）
//        jcb_fightType = new JComboBox<>(new String[]{"人机对战", "人人对战"});
        jcb_fightType = new ComboBox<>(new String[]{"人人对战"});
        jcb_fightType.setBackground(JBColor.WHITE);
        jcb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jcb_fightType);
        //谁先手
        JLabel jlb_playFirst = new JLabel("先手选择：");
        jlb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jlb_playFirst);
        jcb_playFirst = new ComboBox<>(new String[]{"我方先手", "对方先手"});
        jcb_playFirst.setBackground(JBColor.WHITE);
        jcb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jcb_playFirst);

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> {
            startGame0();
        });

        startPanel.add(startGameButton);

        if (DataCache.isOnline) {
            // 如果已经是登录状态，就显示创建房间按钮
            List<Integer> numsList = new ArrayList<>();
            numsList.add(2);
            numsList.add(3);
            numsList.add(4);
            startPanel.add(getCreateRoomButton(numsList));
        }

        startPanel.add(getExitButton());
    }

    private void checkStatus(String username) {
        boolean flag = true;
        switch (status) {
            case 1:
//                showTips("游戏结束：" + username + "这个菜鸡赢了！");
                break;
            case 2:
//                showTips("游戏结束：平局~ 卧槽？？？");
                break;
            default:
                flag = false;
                break;
        }

        gamePanel.isGameOver = flag;
        if (gamePanel.isGameOver) {
            gamePanel.add(getGameOverButton());
            gamePanel.updateUI();
        }
    }
}
