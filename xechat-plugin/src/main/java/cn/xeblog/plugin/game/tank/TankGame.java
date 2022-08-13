package cn.xeblog.plugin.game.tank;

import cn.xeblog.commons.entity.game.tank.TankGameDTO;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.tank.model.*;
import cn.xeblog.plugin.game.tank.msg.*;
import com.intellij.openapi.ui.ComboBox;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 坦克大战
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
@DoGame(Game.TANK)
public class TankGame extends AbstractGame<TankGameDTO> {

    /**
     * 游戏大小
     */
    public static final int GAME_WIDTH = 600;  //界面宽
    public static final int GAME_HEIGHT = 600;  //界面高

    /**
     * 是否按了向上的方向键
     */
    private boolean up = false;
    /**
     * 是否按了向下的方向键
     */
    private boolean down = false;
    /**
     * 是否按了向左的方向键
     */
    private boolean left = false;
    /**
     * 是否按了向右的方向键
     */
    private boolean right = false;
    // 开始界面
    private JPanel startPanel;


    // 绘画游戏界面的面板
    public static TankPanel tankPanel;



    private int mapId;

    public TankGame() {
    }

    public TankGame(TankPanel panel) {
        tankPanel = panel;
    }

    private void initStartPanel() {
        initMainPanel();
        mainPanel.setMinimumSize(new Dimension(150, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 200);
        tankPanel = new TankPanel(GAME_WIDTH,GAME_HEIGHT,mapId,this);
        tankPanel.setBorder(BorderFactory.createEtchedBorder());

        mainPanel.add(startPanel);
        JLabel titleLabel = new JLabel("坦克大战！");
        titleLabel.setFont(new Font("", 1, 14));
        startPanel.add(titleLabel);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        JLabel modelLabel = new JLabel("游戏关卡：");
        modelLabel.setFont(new Font("", 1, 13));
        vBox.add(modelLabel);

        vBox.add(Box.createVerticalStrut(5));

        vBox.add(getGameLevelComboBox());

        vBox.add(Box.createVerticalStrut(20));

        JButton startGameButton = new JButton("开始游戏");
        JButton exitButton = getExitButton();
        startGameButton.setActionCommand("start");
        exitButton.setActionCommand("stop");
        startGameButton.addActionListener(tankPanel);
        exitButton.addActionListener(tankPanel);

        vBox.add(startGameButton);
        vBox.add(exitButton);
        mainPanel.updateUI();
    }
    private void initTankPanel() {
        Dimension mainDimension = new Dimension(GAME_WIDTH+10, GAME_HEIGHT+10);

        initMainPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMinimumSize(mainDimension);

        JPanel mainTopPanel = new JPanel();

        JLabel titleLabel = new JLabel("坦克大战！");
        titleLabel.setFont(new Font("", 1, 14));
        mainTopPanel.add(titleLabel);

        JPanel mainBottomPanel = new JPanel();

        JButton backButton = getBackButton();
        mainBottomPanel.add(backButton);

        JButton exitButton = getExitButton();
        exitButton.setActionCommand("stop");
        exitButton.addActionListener(tankPanel);

        mainBottomPanel.add(exitButton);


        mainPanel.add(mainTopPanel, BorderLayout.NORTH);
        mainPanel.add(tankPanel, BorderLayout.CENTER);
        mainPanel.add(mainBottomPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();
        tankPanel.requestFocusInWindow();
    }

    @Override
    public void handle(TankGameDTO body) {

        switch (body.getMsgType()) {
            case CREATE_BOMB:
                BombResponse bombResponse = (BombResponse) body.getData();
                String bombResponseTankId = bombResponse.getTankId();
                String bombResponseBulletId = bombResponse.getBulletId();
                // 删除产生爆炸的子弹
                if (bombResponseTankId.startsWith("robot")) {
                    if (TankPanel.resource.getRobotTanks().get(bombResponseTankId) == null) {
                        return;
                    }
                    TankPanel.resource.getRobotTanks().get(bombResponseTankId).getBullets().remove(bombResponseBulletId);
                } else {
                    if (TankPanel.resource.getPlayerTanks().get(bombResponseTankId) == null) {
                        return;
                    }
                    TankPanel.resource.getPlayerTanks().get(bombResponseTankId).getBullets().remove(bombResponseBulletId);
                }
                Bomb bomb = new Bomb(bombResponse.getX(), bombResponse.getY());
                bomb.setWidth(bombResponse.getWidth());
                TankPanel.resource.getBombs().add(bomb);
                break;
            case REFRESH_BULLET:
                BulletLocationResponse bulletLocationResponse = (BulletLocationResponse) body.getData();
                String bulletId = bulletLocationResponse.getBulletId();
                String tankId = bulletLocationResponse.getTankId();
                int x = bulletLocationResponse.getX();
                int y = bulletLocationResponse.getY();
                Bullet bullet;
                HashMap<String, Bullet> bullets;
                if (tankId.startsWith("robot")) { // 如果是机器坦克的子弹重绘
                    if (TankPanel.resource.getRobotTanks().get(tankId) == null) { // 如果坦克已被击毁
                        return;
                    }
                    bullets = TankPanel.resource.getRobotTanks().get(tankId).getBullets();
                    bullet = bullets.get(bulletId);

                } else {
                    if (TankPanel.resource.getPlayerTanks().get(tankId) == null) { // 如果坦克已被击毁
                        return;
                    }
                    bullets = TankPanel.resource.getPlayerTanks().get(tankId).getBullets();
                    bullet = TankPanel.resource.getPlayerTanks().get(tankId).getBullets().get(bulletId);
                }

                bullet = bullets.get(bulletId);
                if (bullet == null) {
                    bullet = new Bullet(x, y, -1);
                    bullets.put(bulletId, bullet);
                }
                bullet.setX(x);
                bullet.setY(y);
                if (x < 5 || x > 600 - 5 || y < 5 || y > 600 - 5) { // 判断子弹是否碰到边界
                    bullets.remove(bulletId);
                }
                break;

            case REMOVE_BRICK:
                DestroyBrickResponse destroyBrickResponse = (DestroyBrickResponse) body.getData();
                CopyOnWriteArrayList<Brick> bricks = TankPanel.resource.getMap().getBricks();
                for (Brick brick : bricks) {
                    if (brick.getX() == destroyBrickResponse.getX() && brick.getY() == destroyBrickResponse.getY()) {
                        bricks.remove(brick);
                    }
                }
                break;
            case REFRESH_TANK:
                TankLocationResponse tankLocationResponse = (TankLocationResponse) body.getData();
                Tank tank;
                if (tankLocationResponse.getTankId().startsWith("robot")) {
                    tank = TankPanel.resource.getRobotTanks().get(tankLocationResponse.getTankId());
                } else {
                    tank = TankPanel.resource.getPlayerTanks().get(tankLocationResponse.getTankId());
                }
                if (tank != null) {
                    tank.setX(tankLocationResponse.getX());
                    tank.setY(tankLocationResponse.getY());
                    tank.setDirect(tankLocationResponse.getDirect());
                }
                break;
            case DESTROY_TANK:
                DestroyTankResponse destroyTankResponse = (DestroyTankResponse) body.getData();
                String destroyTankResponseTankId = destroyTankResponse.getTankId();
                if (destroyTankResponseTankId.startsWith("robot")) {
                    TankPanel.resource.getRobotTanks().remove(destroyTankResponseTankId);
                } else {
                    TankPanel.resource.getPlayerTanks().remove(destroyTankResponseTankId);
                    invoke(() -> calcGameEnd(false),25);
                }

                // 只有机器坦克数量为0，游戏胜利
                if (TankPanel.resource.getRobotTanks().size() == 0 && TankPanel.resource.getPlayerTanks().size() <= 1) {
                    invoke(() -> calcGameEnd(true),25);
                }
                break;
        }
    }

    private void calcGameEnd(boolean flag) {
        AtomicInteger y= new AtomicInteger();
        y.addAndGet(10);
        if(flag){
            TankPanel.resource.setGameSuccessY(y.get());
        }else{
            TankPanel.resource.setGameOverY(y.get());
        }
        tankPanel.setState(flag? 1:2);
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(tankPanel, flag?"游戏胜利，返回大厅":"游戏结束，返回大厅", flag?"游戏胜利":"游戏失败", JOptionPane.INFORMATION_MESSAGE);
        //游戏结束，进入模式选择界面
        initStartPanel();
    }

    public void sendMsg(TankGameDTO.MsgType msgType, Object data) {
        sendMsg(msgType, GameAction.getNickname(), data);
    }

    private void sendMsg(TankGameDTO.MsgType msgType, String player, Object data) {

        TankGameDTO dto = new TankGameDTO();
        dto.setMsgType(msgType);
        dto.setPlayer(player);
        dto.setData(data);

        invoke(() -> handle(dto));
    }


    @Override
    protected void init() {
        mapId = 1;
        initStartPanel();
    }


    @Override
    protected void start() {
        initTankPanel();
    }


    private ComboBox getGameLevelComboBox() {
        ComboBox comboBox = new ComboBox();
        comboBox.setPreferredSize(new Dimension(40, 30));
        for (MapLevel mapLevel : MapLevel.values()) {
            comboBox.addItem(mapLevel.getText());
            if (mapId == mapLevel.getValue()) {
                comboBox.setSelectedItem(mapLevel.getText());
            }
        }
        comboBox.addItemListener(e -> {
            mapId = MapLevel.getLeve(comboBox.getSelectedItem().toString());
        });
        return comboBox;
    }


    @Getter
    public enum MapLevel {
        MAP_ONE(1,"第一关"),
        MAP_TWO(2,"第二关"),
        MAP_THREE(3,"第三关"),
        MAP_FOUR(4,"第四关");

        private Integer value;

        private String text;

        MapLevel(Integer value, String text) {
            this.text = text;
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public Integer getValue() {
            return value;
        }

        public static Integer getLeve(String text) {
            for (MapLevel level : values()) {
                if (level.getText().equals(text)) {
                    return level.getValue();
                }
            }
            return null;
        }

    }

    private JButton getBackButton() {
        JButton button = new JButton( "关卡选择");
        button.addActionListener(e -> init());
        return button;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

}