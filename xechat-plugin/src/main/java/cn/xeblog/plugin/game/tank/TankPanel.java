package cn.xeblog.plugin.game.tank;


import cn.xeblog.commons.entity.game.tank.TankGameDTO;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.game.tank.entity.MapEntity;
import cn.xeblog.plugin.game.tank.model.GameResource;
import cn.xeblog.plugin.game.tank.model.*;
import cn.xeblog.plugin.game.tank.msg.TankLocationResponse;
import cn.xeblog.plugin.game.tank.thread.BulletRepaintThread;
import cn.xeblog.plugin.game.tank.thread.MonitorRobotThread;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 坦克大战面板
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class TankPanel extends JPanel implements ActionListener {

	/**
	 * 游戏面板的宽度
	 */
	private int width;

	/**
	 * 游戏面板的高度
	 */
	private int height;


	/**
	 * 定时器
	 */
	private Timer timer;

	/**
	 * 移动速度
	 */
	private int speed = 100;

	/**
	 * 停止游戏标记
	 */
	public boolean stop = false;
	/**
	 * 游戏状态 0.初始化 1.游戏胜利 2.游戏失败
	 */
	private int state = -1;

	private int mapId = 0;

	/**
	 * 面板上的资源，坦克...等
	 */
	public static GameResource resource;

	public MonitorRobotThread monitorRobotThread;


	/**
	 * 游戏控制
	 */
	public TankGame controller;
	/**
	 * 绘画类
	 */
	private Draw draw = new Draw();

	public TankPanel(int width, int height, int mapId, TankGame tankGame) {
		this.width = width;
		this.height = height;
		this.timer = new Timer(speed, this);
		this.stop = true;
		this.mapId = mapId;
		this.controller = tankGame;
		initPanel();

	}


	/**
	 * 初始化
	 */
	private void init() {
		this.state = 0;
		this.stop = true;
		this.timer.setDelay(speed);
		resource = loadMap(mapId, GameAction.getNickname());
		monitorRobotThread = new MonitorRobotThread(resource, controller);
		monitorRobotThread.start();

	}

	/**
	 * 初始化游戏面板
	 */
	private void initPanel() {
		TankPanel tankPanelUI = this;
		this.setPreferredSize(new Dimension(this.width, this.height));
		this.addKeyListener(getKeyListener());
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				tankPanelUI.requestFocusInWindow();
			}
		});
	}


	/**
	 * 获取按键事件监听
	 *
	 * @return
	 */
	private KeyListener getKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				String tankId = GameAction.getNickname();
				Tank myTank = resource.getPlayerTanks().get(tankId);

				if (myTank != null) {
					if ((e.getKeyCode() == KeyEvent.VK_UP) && myTank.isAlive()) {
						judgeCrash(myTank,Tank.NORTH);
					} else if ((e.getKeyCode() == KeyEvent.VK_DOWN) && myTank.isAlive()) {
						judgeCrash(myTank,Tank.SOUTH);
					} else if ((e.getKeyCode() == KeyEvent.VK_LEFT) && myTank.isAlive() && myTank.getY() <= 580) {
						judgeCrash(myTank,Tank.WEST);
					} else if ((e.getKeyCode() == KeyEvent.VK_RIGHT) && myTank.isAlive() && myTank.getY() <= 580) {
						judgeCrash(myTank,Tank.EAST);
					}
					if (e.getKeyCode() == KeyEvent.VK_X && myTank.isAlive() && myTank.getY() <= 580) {
						if (myTank.getRemainBulletCount() > 0) {
							Bullet bullet = myTank.shot(); // 这时才会往容器中添加子弹对象
							//如果子弹个数小于等于0，不发射子弹
							if (myTank.getRemainBulletCount() <= 0) {
								return;
							}
							Thread t = new Thread(new BulletRepaintThread(bullet, myTank,resource,controller));
							t.start();
						}
					}
				}
			}
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_UP) {
					controller.setUp(false);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					controller.setDown(false);
				} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					controller.setLeft(false);
				} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					controller.setRight(false);
				}
			}
		};
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		if (resource != null) {
			// 画出地图
			if (resource.getMap() != null) {
				draw.drawMap(g, resource.getMap(), this);
			}
			// 画出坦克
			if (resource.getPlayerTanks() != null) {
				draw.drawPlayerTank(g, resource.getPlayerTanks(), this);
			}
			// 画出机器坦克
			if (resource.getRobotTanks() != null) {
				draw.drawRobotTank(g, resource.getRobotTanks(), this);
			}
			if (resource.getBombs() != null) {
				draw.drawBombs(g, resource.getBombs(), this);
			}
			// 画出失败的图片
			if (resource.getGameOverY() != 0) {
				state = 2;
			}
			// 画出游戏胜利的图片
			if (resource.getGameSuccessY() != 0) {
				state = 1;
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(null != e.getActionCommand()){
			switch (e.getActionCommand()) {
				case "start":
					this.stop = false;
					init();
					controller.start();
					this.timer.start();
					break;
				case "stop":
					this.stop = true;
					this.timer.stop();
					break;
			}
		}
		if(state > 0){
			this.stop = true;
			this.monitorRobotThread.stopFlag = true;
			this.timer.stop();
		}
		repaint();
	}


	/**
	 * 初始化地图
	 *
	 * @param mapId
	 * @param name
	 */
	public GameResource loadMap(Integer mapId, String name) {

		java.util.List<MapEntity> list = MapFactory.getInstance().getLevelMap(mapId);
		java.util.List<Brick> bricks = new ArrayList<>();
		java.util.List<Water> waters = new ArrayList<>();
		java.util.List<Iron> irons = new ArrayList<>();
		java.util.Map<String, RobotTank> robots = new HashMap<>();
		Map<String, Tank> playerTanks = new HashMap<>();
		int robotID = 1;
		for (MapEntity entity : list) {
			switch (entity.getType()) {
				case MapEntity.BRICK:
					Brick brick = new Brick(entity.getX(), entity.getY());
					bricks.add(brick);
					break;
				case MapEntity.WATER:
					Water water = new Water(entity.getX(), entity.getY());
					waters.add(water);
					break;
				case MapEntity.IRON:
					Iron iron = new Iron(entity.getX(), entity.getY());
					irons.add(iron);
					break;
				case MapEntity.ROBOT:
					RobotTank robot = new RobotTank(entity.getX(), entity.getY(), Tank.NORTH, "robot" + robotID);
					robots.put("robot" + robotID, robot);
					robotID++;
					break;
				case MapEntity.PLAYER_TANK:
					Tank tank = new Tank(entity.getX(), entity.getY(), Tank.NORTH, name);
					playerTanks.put(name, tank);
			}
		}
		GameResource gameResource = new GameResource();
		CopyOnWriteArrayList<Brick> bricksMap = new CopyOnWriteArrayList<>(bricks);
		CopyOnWriteArrayList<Water> watersMap = new CopyOnWriteArrayList<>(waters);
		CopyOnWriteArrayList<Iron> ironsMap = new CopyOnWriteArrayList<>(irons);
		ConcurrentHashMap<String, Tank> playerTankMap = new ConcurrentHashMap<>(playerTanks);
		ConcurrentHashMap<String, RobotTank> robotTanks = new ConcurrentHashMap<>(robots);
		gameResource.setBricks(bricksMap);
		gameResource.setWaters(watersMap);
		gameResource.setIrons(ironsMap);
		TankMap map = new TankMap();
		map.setBricks(bricksMap);
		map.setWaters(watersMap);
		map.setIrons(ironsMap);
		gameResource.setMap(map);
		gameResource.setPlayerTanks(playerTankMap);
		gameResource.setRobotTanks(robotTanks);
		return gameResource;
	}

	/**
	 * 判断我的坦克是否与地形碰撞
	 *
	 * @param direct
	 */
	public void judgeCrash( Tank tank, int direct) {

		tank.setDirect(direct);
		// 判断一下部是否会发生碰撞
		Tank myTank = new Tank(tank.getX(), tank.getY(), direct, "");
		myTank.setDirect(direct);
		myTank.setCrash(false);
		CopyOnWriteArrayList<Brick> bricks = resource.getBricks();
		CopyOnWriteArrayList<Water> waters = resource.getWaters();
		CopyOnWriteArrayList<Iron> irons = resource.getIrons();
		// 判断我的坦克是否与其他坦克重叠
		for (Tank t : resource.getPlayerTanks().values()) {
			if (t != tank) {
				if (myTank.Overlap(t) && t.isAlive()) {
					myTank.setCrash(true);
					break;
				}
			}
		}
		// 判断我的坦克是否与机器坦克碰撞
		for (RobotTank robot : resource.getRobotTanks().values()) {
			if (myTank.Overlap(robot) && robot.isAlive()) {
				myTank.setCrash(true);
				break;
			}
		}
		// 判断我的坦克是否与砖块重叠
		for (int j = 0; j < bricks.size(); j++) {
			if (myTank.Overlap(bricks.get(j)) == true) {
				myTank.setCrash(true);
				break;
			}
		}
		// 判断我的坦克是否与铁块重叠
		for (int j = 0; j < irons.size(); j++) {
			if (myTank.Overlap(irons.get(j)) == true) {
				myTank.setCrash(true);
				break;
			}
		}
		// 判断我的坦克是否与河流重叠
		for (int j = 0; j < waters.size(); j++) {
			if (myTank.Overlap(waters.get(j)) == true) {
				myTank.setCrash(true);
				break;
			}
		}
		// 坦克没有与其他物体发生碰撞，就往对应方向移动
		if (direct == Tank.NORTH && myTank.isCrash() == false) {
			tank.goNorth();
		} else if (direct == Tank.SOUTH && myTank.isCrash() == false) {
			tank.goSouth(600);
		} else if (direct == Tank.WEST && myTank.isCrash() == false) {
			tank.goWest(600);
		} else if (direct == Tank.EAST && myTank.isCrash() == false) {
			tank.goEast(600, 600);
		}
		TankLocationResponse tankLocationResponse = new TankLocationResponse();
		tankLocationResponse.setTankId(myTank.getTankId());
		tankLocationResponse.setX(tank.getX());
		tankLocationResponse.setY(tank.getY());
		tankLocationResponse.setDirect(direct);
		controller.sendMsg(TankGameDTO.MsgType.REFRESH_TANK,tankLocationResponse);
	}


	public TankGame getController() {
		return controller;
	}

	public void setController(TankGame controller) {
		this.controller = controller;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
}
