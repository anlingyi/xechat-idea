package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.game.chess.ChessDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 功能：中国象棋入口<br>
 * 作者：Hao.<br>
 */
@DoGame(Game.CHINESE_CHESS)
public class ChineseChess extends AbstractGame<ChessDTO> {

    private JPanel mainPanel;

    // 开始界面
    private JPanel startPanel;

    // 游戏界面
    private GamePanel gamePanel;

    // 开始界面 组件
    private JComboBox<String> jcb_fightType, jcb_playFirst, jcb_UIType;

    // 缓存
    ChessCache chessCache;

    // 游戏状态 -2.初始化 -1.待开始 0.进行中 1.赢 2.平
    int status = -2;

    @Override
    protected void start() {
        startGame0(ChessCache.Mode.ONLINE);
    }

    @Override
    protected void init() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 400);

        startPanel();

        mainPanel.add(startPanel);

        mainPanel.updateUI();
    }

    @Override
    protected void allPlayersGameStarted() {
        ChessCache.Player currentPlayer = Arrays.stream(ChessCache.Player.values()).filter(o -> o.getName().equals(jcb_playFirst.getSelectedItem().toString())).findFirst().get();

        ChessCache.Player opposingPlayer = currentPlayer == ChessCache.Player.RED ? ChessCache.Player.BLACK : ChessCache.Player.RED;

        ChessDTO.UI currentUI = Arrays.stream(ChessDTO.UI.values()).filter(o -> o.getName().equals(jcb_UIType.getSelectedItem().toString())).findFirst().get();
        if (isHomeowner()) {
            // 自旋等待一段时间，再发送游戏数据
            invoke(() -> {
                ChessDTO msg = new ChessDTO();
                msg.setType(opposingPlayer.getValue());
                msg.setCurrentUI(currentUI);
                sendMsg(msg);
                handle(new ChessDTO(0, 0, currentPlayer.getValue(), -1, ChessDTO.Option.DEFAULT, currentUI));
            }, 500);
        }
    }

    @Override
    public void handle(ChessDTO body) {
        gamePanel.jb_undo.setEnabled(false);
        if (status > -1) {
            if (body.getOption().equals(ChessDTO.Option.UNDO)) {
                int i = JOptionPane.showConfirmDialog(gamePanel, "对方请求悔棋，是否同意？", "提示", JOptionPane.YES_NO_OPTION);
                if (i != 0){
                    // 拒绝悔棋，需发送拒绝通知
                    send(new Point(ChessDTO.Option.UNDO_REJECT));
                    return;
                }
                chessCache.put = true;
                // 同意悔棋
                send(new Point(ChessDTO.Option.UNDO_CONSENT));
                gamePanel.otherSideUndo();
                return;
            }
            if (body.getOption().equals(ChessDTO.Option.UNDO_REJECT)) {
                gamePanel.jb_surrender.setEnabled(true);
                JOptionPane.showMessageDialog(gamePanel, "对方拒绝悔棋！");
                return;
            }
            if (body.getOption().equals(ChessDTO.Option.UNDO_CONSENT)) {
                gamePanel.jb_surrender.setEnabled(true);
                chessCache.put = false;
                gamePanel.gameLogic.undo();
                return;
            }
            if (body.getOption().equals(ChessDTO.Option.SURRENDER)) {
                JOptionPane.showMessageDialog(gamePanel, "对方投降了！");
                invoke(() -> gamePanel.gameOver());
                return;
            }
            if (body.getOption().equals(ChessDTO.Option.GAME_OVER)) {
                JOptionPane.showMessageDialog(gamePanel, "胜败乃兵家常事，少侠请重新来过！");
                invoke(() -> gamePanel.gameOver());
                return;
            }

            if (body.getOption().equals(ChessDTO.Option.CHECK)) {
                gamePanel.otherSideCheck();
            }

            changePlayer(body);
            gamePanel.setChess(new Point(body.getX(), body.getY(), body.getType(), body.getIndex()));

            if (gamePanel.isGameOver) {
                return;
            }
        } else {
            status = 0;
            chessCache.currentMode = ChessCache.Mode.ONLINE;
            chessCache.currentBattle = ChessCache.Battle.PVP;
            chessCache.currentUI = body.getCurrentUI();
            this.gamePanel.jlb_redStateText.setText("思考中");
            if (body.getType() == ChessCache.Player.BLACK.getValue()) {
                chessCache.currentPlayer = ChessCache.Player.BLACK;
                gamePanel.initChess();
                chessCache.put = true;
                return;
            }else{
                chessCache.currentPlayer = ChessCache.Player.RED;
            }
        }

        chessCache.put = false;
    }

    /**
     * 开始游戏
     * @author Hao.
     * @date 2022/9/5 10:09
     * @param mode
     * @return void
     */
    private void startGame0(ChessCache.Mode mode) {
        status = -1;

        chessCache = new ChessCache();

        if(mode == ChessCache.Mode.OFFLINE){
            chessCache.currentMode = ChessCache.Mode.OFFLINE;
            chessCache.currentPlayer = Arrays.stream(ChessCache.Player.values()).filter(o -> o.getName().equals(jcb_playFirst.getSelectedItem().toString())).findFirst().get();
            chessCache.currentBattle = Arrays.stream(ChessCache.Battle.values()).filter(o -> o.getName().equals(jcb_fightType.getSelectedItem().toString())).findFirst().get();
            chessCache.currentUI = Arrays.stream(ChessDTO.UI.values()).filter(o -> o.getName().equals(jcb_UIType.getSelectedItem().toString())).findFirst().get();
        }

        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(new Dimension(320,360));

        this.gamePanel = new GamePanel(this);

        // 游戏面板
        mainPanel.add(gamePanel);

        if(mode == ChessCache.Mode.OFFLINE){
            chessCache.put = false;
            gamePanel.remove(gamePanel.jb_surrender);
            gamePanel.add(gamePanel.exitButton());
        }

        mainPanel.updateUI();
    }

    public void send(Point point) {
        ChessDTO dto = new ChessDTO();
        dto.setX(point.x);
        dto.setY(point.y);
        dto.setType(point.type);
        dto.setIndex(point.index);
        dto.setOption(point.option);
        sendMsg(dto);
    }

    /**
     * 功能：右边功能区布局<br>
     */
    private void startPanel()
    {
        //logo图片
        ImageIcon imageIconLogo = new ImageIcon(new ImageIcon(this.getClass().getResource("/games/chinese-chess/logo.png")).getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH));//缩放图片来适应标签大小
        JLabel labelLogo = new JLabel(imageIconLogo);
        startPanel.add(labelLogo);
        //对战方式
        JLabel jlb_UIType = new JLabel("界面类型：");
        jlb_UIType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jlb_UIType);
        jcb_UIType = new ComboBox<>(Arrays.stream(ChessDTO.UI.values()).map(ChessDTO.UI::getName).toArray(String[]::new));
        jcb_UIType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jcb_UIType);
        //对战方式
        JLabel jlb_fightType = new JLabel("对战方式：");
        jlb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jlb_fightType);
        jcb_fightType = new ComboBox<>(Arrays.stream(ChessCache.Battle.values())
                .filter(battle -> battle != ChessCache.Battle.PVC)
                .map(ChessCache.Battle::getName)
                .toArray(String[]::new));
        jcb_fightType.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jcb_fightType);
        //谁先手
        JLabel jlb_playFirst = new JLabel("玩家选择：");
        jlb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jlb_playFirst);

        jcb_playFirst = new ComboBox<>(Arrays.stream(ChessCache.Player.values()).map(ChessCache.Player::getName).toArray(String[]::new));
        jcb_playFirst.setBackground(JBColor.WHITE);
        jcb_playFirst.setFont(new Font("微软雅黑",Font.PLAIN,12));
        startPanel.add(jcb_playFirst);

        JButton startGameButton = new JButton("开始游戏");
        startGameButton.addActionListener(e -> startGame0(ChessCache.Mode.OFFLINE));

        startPanel.add(startGameButton);

        if (DataCache.isOnline) {
            // 如果已经是登录状态，就显示创建房间按钮
            List<Integer> numsList = new ArrayList<>();
            numsList.add(2);
            startPanel.add(getCreateRoomButton(numsList));
        }

        startPanel.add(getExitButton());
    }

    private void changePlayer(ChessDTO body) {
        //棋子信息对调
        body.setX(9 - body.getX());
        body.setY(8 - body.getY());
    }

    public JButton gameOverButton() {
        return getGameOverButton();
    }

    @Override
    public void playerLeft(User player) {
        super.playerLeft(player);
        if (!gamePanel.isGameOver && status > -1) {
            ChessDTO dto = new ChessDTO();
            dto.setOption(ChessDTO.Option.SURRENDER);
            handle(dto);
        }
    }

    @Override
    protected JPanel getComponent() {
        return mainPanel;
    }
}
