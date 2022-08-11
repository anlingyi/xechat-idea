package cn.xeblog.plugin.game.tank.thread;

import cn.xeblog.commons.entity.game.tank.TankGameDTO;
import cn.xeblog.plugin.game.tank.TankGame;
import cn.xeblog.plugin.game.tank.model.*;
import cn.xeblog.plugin.game.tank.msg.BombResponse;
import cn.xeblog.plugin.game.tank.msg.BulletLocationResponse;
import cn.xeblog.plugin.game.tank.msg.DestroyBrickResponse;
import cn.xeblog.plugin.game.tank.msg.DestroyTankResponse;

import java.util.concurrent.CopyOnWriteArrayList;


public class BulletRepaintThread implements Runnable {
	private int initX;
	private int initY;
	private Bullet bullet;
	private Tank tank;
	private GameResource resource;

	private TankGame tankGame;

	public BulletRepaintThread(Bullet bullet, Tank tank,GameResource resource,TankGame tankGame) {
		this.tank = tank;
		this.bullet = bullet;
		initX = bullet.getX();
		initY = bullet.getY();
		this.tankGame = tankGame;
		this.resource = resource;
	}

	@Override
	public void run() {

		while (true) {
			if(tank.isAlive()==false){
				break;
			}
			switch (bullet.getMovingDirect()) { // 选择子弹的方向
			case Element.NORTH:
				bullet.setY(bullet.getY() - bullet.getSpeed());
				break;
			case Element.SOUTH:
				bullet.setY(bullet.getY() + bullet.getSpeed());
				break;
			case Element.WEST:
				bullet.setX(bullet.getX() - bullet.getSpeed());
				break;
			case Element.EAST:
				bullet.setX(bullet.getX() + bullet.getSpeed());
				break;
			}
			if (hEuclidianDistance(initX, initY, bullet.getX(), bullet.getY()) >= Bullet.RANGE) { // 子弹运行到爆炸的范围
				afterHit();
				break;
			}
			if (isHit()) {
				afterHit();
				break;
			}
			BulletLocationResponse response = new BulletLocationResponse();
			response.setTankId(tank.getTankId());
			response.setBulletId(bullet.getBulletId());
			response.setX(bullet.getX());
			response.setY(bullet.getY());


			tankGame.sendMsg(TankGameDTO.MsgType.REFRESH_BULLET,response);
			if (bullet.getX() < 5 || bullet.getX() > 600 - 5 || bullet.getY() < 5 || bullet.getY() > 600 - 5) { // 判断子弹是否碰到边界
				afterHit();
				bullet.setAlive(false); // 子弹死亡
				tank.getBullets().remove(bullet);
				break;
			}
			try {
				Thread.sleep(30); // 每隔30毫秒移动一次
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// 线程结束，子弹消失，删除子弹
		tank.getBullets().remove(bullet.getBulletId());
		synchronized (this.tank.getRemainBulletCount()) {
			this.tank.increseRemainBulletCount();
		}
	}

	/**
	 * 删除爆炸范围内的物体
	 *
	 * @param
	 */
	private void afterHit() {
		BombResponse bombResponse = new BombResponse();
		bombResponse.setX(bullet.getX());
		bombResponse.setY(bullet.getY());
		bombResponse.setTankId(tank.getTankId());
		bombResponse.setBulletId(bullet.getBulletId());
		bombResponse.setWidth(Bullet.EXPLOSIONG_RANGE * 2);

		tankGame.sendMsg(TankGameDTO.MsgType.CREATE_BOMB,bombResponse);

		CopyOnWriteArrayList<Brick> bricks = resource.getBricks();
		for (Brick brick : bricks) {
			if (Math.abs(brick.getY() - bullet.getY()) < Bullet.EXPLOSIONG_RANGE + brick.getWidth() / 2
					&& Math.abs(brick.getX() - bullet.getX()) < Bullet.EXPLOSIONG_RANGE + brick.getWidth() / 2) {
				bricks.remove(brick);
				// 广播击中砖块消息
				DestroyBrickResponse destroyBrickResponse = new DestroyBrickResponse();
				destroyBrickResponse.setX(brick.getX());
				destroyBrickResponse.setY(brick.getY());
				// 广播子弹信息
				tankGame.sendMsg(TankGameDTO.MsgType.REMOVE_BRICK,destroyBrickResponse);
			}
		}

		for (Tank playerTank : resource.getPlayerTanks().values()) {
			if (tank != playerTank
					&& Math.abs(playerTank.getY() - bullet.getY()) < Bullet.EXPLOSIONG_RANGE + playerTank.getWidth() / 2
					&& Math.abs(playerTank.getX() - bullet.getX()) < Bullet.EXPLOSIONG_RANGE + playerTank.getWidth() / 2
					&& playerTank.isAlive()) { // 如果该坦克还存活
				playerTank.setAlive(false);
				//被击中的玩家坦克，删除子弹
				for(String key:resource.getPlayerTanks().get(playerTank.getTankId()).getBullets().keySet()){
					resource.getPlayerTanks().get(playerTank.getTankId()).getBullets().remove(key);
				}
				resource.getPlayerTanks().remove(playerTank.getTankId());
				// 广播击毁信息
				DestroyTankResponse destroyTankResponse = new DestroyTankResponse(playerTank.getTankId());
				tankGame.sendMsg(TankGameDTO.MsgType.DESTROY_TANK,destroyTankResponse);
			}
		}
		// 如果该子弹不是机器坦克产生的，机器坦克的子弹不能击毁机器坦克
		if (!tank.getTankId().startsWith("robot")) {
			for (RobotTank robot : resource.getRobotTanks().values()) {
				if (Math.abs(robot.getY() - bullet.getY()) < Bullet.EXPLOSIONG_RANGE + robot.getWidth() / 2
						&& Math.abs(robot.getX() - bullet.getX()) < Bullet.EXPLOSIONG_RANGE + robot.getWidth() / 2) {
					robot.setAlive(false);
					//被击中的机器坦克，删除子弹
					for(String key:resource.getRobotTanks().get(robot.getTankId()).getBullets().keySet()){
						resource.getRobotTanks().get(robot.getTankId()).getBullets().get(key);
						resource.getRobotTanks().get(robot.getTankId()).getBullets().remove(key);
					}
					resource.getRobotTanks().remove(robot.getTankId());
					// 广播击毁信息
					DestroyTankResponse destroyTankResponse = new DestroyTankResponse(robot.getTankId());
					tankGame.sendMsg(TankGameDTO.MsgType.DESTROY_TANK,destroyTankResponse);
				}
			}
		}
	}

	// 子弹是否击中物体
	public boolean isHit() {
		// 子弹是否击中其他玩家坦克
		for (Tank otherTank : resource.getPlayerTanks().values()) {
			if (tank != otherTank && otherTank.isAlive() == true) { // 如果该坦克还存活
				if (Math.abs(bullet.getX() - otherTank.getX()) <= otherTank.getWidth() / 2
						&& Math.abs(bullet.getY() - otherTank.getY()) <= otherTank.getWidth() / 2) {// 子弹击中坦克
					return true;
				}
			}
		}
		if (!tank.getTankId().startsWith("robot")) { // 如果该子弹不是机器坦克产生的，机器坦克的子弹不能击毁机器坦克
			// 子弹是否击中机器坦克
			for (Tank otherTank : resource.getRobotTanks().values()) {// 子弹是否击中机器坦克
				if (otherTank.isAlive() == true) { // 如果该坦克还存活
					if (Math.abs(bullet.getX() - otherTank.getX()) <= otherTank.getWidth() / 2
							&& Math.abs(bullet.getY() - otherTank.getY()) <= otherTank.getWidth() / 2) {// 子弹击中坦克
						return true;
					}
				}

			}
		}

		for (int l = 0; l < resource.getBricks().size(); l++) { // 取出每个砖块对象与子弹比较
			Brick brick = resource.getBricks().get(l);
			if (Math.abs(bullet.getX() - brick.getX()) <= brick.getWidth() / 2
					&& Math.abs(bullet.getY() - brick.getY()) <= brick.getWidth() / 2) {// 子弹击中砖块
				return true;
			}
		}
		for (int l = 0; l < resource.getIrons().size(); l++) { // 取出每个铁块对象与子弹比较
			Iron iron = resource.getIrons().get(l);
			if (Math.abs(bullet.getX() - iron.getX()) <= iron.getWidth() / 2
					&& Math.abs(bullet.getY() - iron.getY()) <= iron.getWidth() / 2) {// 子弹击中铁块
				return true;
			}
		}
		return false;
	}

	public boolean bulletHit() {

		// 子弹是否击中其他玩家坦克
		for (Tank otherTank : resource.getPlayerTanks().values()) {
			if (tank != otherTank && otherTank.isAlive() == true) { // 如果该坦克还存活
				if (Math.abs(bullet.getX() - otherTank.getX()) <= otherTank.getWidth() / 2
						&& Math.abs(bullet.getY() - otherTank.getY()) <= otherTank.getWidth() / 2) {// 子弹击中坦克
					this.afterShotTank(otherTank);
					return true;
				}
			}
		}

		if (!tank.getTankId().startsWith("robot")) { // 如果该子弹不是机器坦克产生的，机器坦克的子弹不能击毁机器坦克
			// 子弹是否击中机器坦克
			for (Tank otherTank : resource.getRobotTanks().values()) {// 子弹是否击中机器坦克
				if (otherTank.isAlive() == true) { // 如果该坦克还存活
					if (Math.abs(bullet.getX() - otherTank.getX()) <= otherTank.getWidth() / 2
							&& Math.abs(bullet.getY() - otherTank.getY()) <= otherTank.getWidth() / 2) {// 子弹击中坦克
						this.afterShotTank(otherTank);
						return true;
					}
				}

			}
		}

		for (int l = 0; l < resource.getBricks().size(); l++) { // 取出每个砖块对象与子弹比较
			Brick brick = resource.getBricks().get(l);
			if (Math.abs(bullet.getX() - brick.getX()) <= brick.getWidth() / 2
					&& Math.abs(bullet.getY() - brick.getY()) <= brick.getWidth() / 2) {// 子弹击中砖块
				this.afterShotElement(brick);// 击中事物
				return true;
			}
		}
		for (int l = 0; l < resource.getIrons().size(); l++) { // 取出每个铁块对象与子弹比较
			Iron iron = resource.getIrons().get(l);
			if (Math.abs(bullet.getX() - iron.getX()) <= iron.getWidth() / 2
					&& Math.abs(bullet.getY() - iron.getY()) <= iron.getWidth() / 2) {// 子弹击中铁块
				this.afterShotElement(iron); // 击中事物
				return true;
			}
		}
		return false;
	}

	public void afterShotTank(Tank otherTank) {
		// 存活状态设为false，当该坦克发射的子弹消失后，才清除
		otherTank.setAlive(false);
		// 删除坦克
		if (otherTank.getTankId().startsWith("robot")) {
			resource.getRobotTanks().remove(otherTank.getTankId());
		} else {
			resource.getPlayerTanks().remove(otherTank.getTankId());
		}
		// 广播击毁信息
		DestroyTankResponse destroyTankResponse = new DestroyTankResponse(otherTank.getTankId());
		// 广播子弹信息
		tankGame.sendMsg(TankGameDTO.MsgType.DESTROY_TANK,destroyTankResponse);
		// 广播爆炸信息
		BombResponse bombResponse = new BombResponse();
		bombResponse.setX(bullet.getX());
		bombResponse.setY(bullet.getY());
		bombResponse.setTankId(tank.getTankId());
		bombResponse.setBulletId(bullet.getBulletId());
		bombResponse.setWidth(40);
		// 广播爆炸信息
		tankGame.sendMsg(TankGameDTO.MsgType.CREATE_BOMB,bombResponse);
	}

	public void afterShotElement(Element element) {
		BombResponse bombResponse = new BombResponse();
		bombResponse.setX(bullet.getX());
		bombResponse.setY(bullet.getY());
		bombResponse.setTankId(tank.getTankId());
		bombResponse.setBulletId(bullet.getBulletId());
		switch (element.getType()) {
		case Element.BRICK: // 砖块
			CopyOnWriteArrayList<Brick> bricks = resource.getBricks();
			// 删除被击中的砖块
			for (Brick brick : bricks) {
				if (brick.getX().equals(element.getX()) && brick.getY().equals(element.getY())) {
					bricks.remove(brick);
					break;
				}
			}
			// 广播击中砖块消息
			DestroyBrickResponse destroyBrickResponse = new DestroyBrickResponse();
			destroyBrickResponse.setX(element.getX());
			destroyBrickResponse.setY(element.getY());
			tankGame.sendMsg(TankGameDTO.MsgType.REMOVE_BRICK,destroyBrickResponse);
			// 设置爆炸宽度信息
			bombResponse.setWidth(40);
			break;
		case Element.IRON: // 铁块
			// 设置爆炸宽度信息
			bombResponse.setWidth(20);
			break;
		}
		// 广播爆炸信息
		tankGame.sendMsg(TankGameDTO.MsgType.CREATE_BOMB,bombResponse);
	}

	/**
	 * 欧式距离,小于等于实际值
	 */
	double hEuclidianDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public Bullet getBullet() {
		return bullet;
	}

	public void setBullet(Bullet bullet) {
		this.bullet = bullet;
	}

}
