package cn.xeblog.plugin.game.tank.thread;


import cn.xeblog.commons.entity.game.tank.TankGameDTO;
import cn.xeblog.plugin.game.tank.TankGame;
import cn.xeblog.plugin.game.tank.model.*;
import cn.xeblog.plugin.game.tank.msg.TankLocationResponse;

import java.util.*;

/**
 * 监测坦克警戒范围内是否有玩家坦克
 *
 * @author Administrator
 *
 */
public class MonitorRobotThread extends Thread {
	private GameResource resource;
	private TankGame tankGame;
	public boolean stopFlag = false;

	public static void main(String[] args) {

		RobotTank robot = new RobotTank(1, 1, 0, "");
		robot.setCircleCenterX(0);
		robot.setCircleCenterY(0);
		for (int i = 0; i < 1000; i++) {
			int minX = robot.getCircleCenterX() - RobotTank.WARNRANGE;
			int maxX = robot.getCircleCenterX() + RobotTank.WARNRANGE;
			int minY = robot.getCircleCenterY() - RobotTank.WARNRANGE;
			int maxY = robot.getCircleCenterY() + RobotTank.WARNRANGE;
			int x = new Random().nextInt(maxX - minX + 1) + minX;
			int y;
			do {
				y = new Random().nextInt(maxY - minY + 1) + minY;
			} while (hEuclidianDistance(x, y, robot.getCircleCenterX(),
					robot.getCircleCenterY()) > RobotTank.WARNRANGE);
			System.out.println(x + "," + y);
		}
	}

	public MonitorRobotThread(GameResource resource,TankGame tankGame) {
    	this.resource = resource;
    	this.tankGame = tankGame;
	}

	@Override
	public void run() {
		HashMap<String, LinkedList<Tank>> robotRoutes = new HashMap<>(); // <tankId,坦克对应的路线>
		HashMap<String, Boolean> isToPlayerRoutes = new HashMap<>(); // 当前路线是否寻找是玩家的坦克,false为随机路线，true为走向玩家的路线
		for (RobotTank robot : resource.getRobotTanks().values()) { // 初始化
			isToPlayerRoutes.put(robot.getTankId(), false);
		}
		while (true) {
			if (stopFlag == true) {
				break;
			}
			for (RobotTank robot : resource.getRobotTanks().values()) {
				// 判断每个机器坦克的警戒范围内是否有玩家坦克
				for (Tank tank : resource.getPlayerTanks().values()) {
					// 警戒范围内有敌人，并且当前规划的路线是随机路线
					if (hEuclidianDistance(robot.getCircleCenterX(), robot.getCircleCenterY(), tank.getX(),
							tank.getY()) < RobotTank.WARNRANGE && !isToPlayerRoutes.get(robot.getTankId())) {
						// 重新规划前往玩家坦克的路线
						LinkedList<Tank> routes = findRoute(robot, tank);
						robotRoutes.put(robot.getTankId(), routes);
						isToPlayerRoutes.put(robot.getTankId(), true);
						break;
					}
				}
				// 警戒范围内没有玩家坦克，并且没有路线
				if (isToPlayerRoutes.get(robot.getTankId()) == false && robotRoutes.get(robot.getTankId()) == null) {
					// 警戒范围内没有玩家坦克，在警戒范围内随机选一个点进行规划路线
					int minX = robot.getCircleCenterX() - RobotTank.WARNRANGE;
					int maxX = robot.getCircleCenterX() + RobotTank.WARNRANGE;
					int minY = robot.getCircleCenterY() - RobotTank.WARNRANGE;
					int maxY = robot.getCircleCenterY() + RobotTank.WARNRANGE;
					int x = new Random().nextInt(maxX - minX + 1) + minX;
					int y;
					do {
						y = new Random().nextInt(maxY - minY + 1) + minY;
					} while (hEuclidianDistance(x, y, robot.getCircleCenterX(),
							robot.getCircleCenterY()) > RobotTank.WARNRANGE);
					Tank endTank = new Tank(x, y, Tank.NORTH, "");
					LinkedList<Tank> routes = findRoute(robot, endTank);
					robotRoutes.put(robot.getTankId(), routes);
					isToPlayerRoutes.put(robot.getTankId(), false);
				}
				if (robotRoutes.get(robot.getTankId()) != null) { // 判断每个机器坦克是否还有未走完的路线
					// 分时间片执行每个坦克的寻路步骤
					boolean flag = drawRoute(robotRoutes.get(robot.getTankId()), 1, robot);
					// 如果该路径走完，或者在路径上碰撞，删除该路径
					if (robotRoutes.get(robot.getTankId()).size() <= 0 || flag == true) {
						robotRoutes.remove(robot.getTankId());
						isToPlayerRoutes.put(robot.getTankId(), false);
					}
				}
			}
		}
		System.out.println("monitor over");
	}

	/**
	 * 寻路
	 *
	 * @param startTank
	 * @param endTank
	 */
	private LinkedList<Tank> findRoute(Tank startTank, Tank endTank) {
		Set<Position> closeTable = new HashSet<>(); // closed表
		Comparator<Position> comp = new Comparator<Position>() {

			@Override
			public int compare(Position p1, Position p2) {
				return new Double(p1.getF()).compareTo(new Double(p2.getF()));
			}
		};
		PriorityQueue<Position> opentable = new PriorityQueue<>(100,comp); // open表

		Position startPosition = new Position(startTank.getX(), startTank.getY()); // 起始点
		Position endPosition = new Position(endTank.getX(), endTank.getY()); // 终点
		startPosition.setDirect(startTank.getDirect());
		startPosition.setH(h(startPosition, endPosition));
		startPosition.setG(0);
		startPosition.setF(h(startPosition, endPosition));
		opentable.add(startPosition);

		int speed = startTank.getSpeed(); // 坦克的移动速度
		boolean finish = false; // 寻路结束的标识
		final int[][] directs = { { 0, -speed }, { 0, speed }, { -speed, 0 }, { speed, 0 } }; // 可以扩展的四个方向
		Position lastPosition = null;
		List<Brick> bricks = resource.getBricks();
		List<Water> waters = resource.getWaters();
		List<Iron> irons = resource.getIrons();
		while (!opentable.isEmpty() && !finish) {
			Position p = opentable.poll(); // 取f最小的节点进行扩展
			closeTable.add(p); // 将已扩展的节点放入closed表
			for (int direct = 0; direct < 4; direct++) { // 判断坦克往四个方向会不会碰到障碍物，如果会碰撞就不进行扩展

				Position newPosition = new Position(p.getX() + directs[direct][0], p.getY() + directs[direct][1]);
				Tank robot1 = new Tank(p.getX(), p.getY(), direct, "");
				// 如果坦克的下一步，没有跑出地图
				if (newPosition.getX() >= 20 && newPosition.getX() <= 600 - 20 && newPosition.getY() >= 20
						&& newPosition.getY() < 600 - 20 && !closeTable.contains(newPosition)
						&& !opentable.contains(newPosition)) {
					// 如果已将到达终点,也就是和目标坦克发生碰撞,并且发射子弹能击中终点坦克
					if (robot1.Overlap(endTank)) {
						lastPosition = p;
						// lastPosition.setLast(p);
						finish = true;
						break;
					}
					// 判断robot往direct方向走，会不会碰到砖块、水或者钢铁，如果会碰撞，该direct的下一个坐标就不进行扩展
					for (int j = 0; j < bricks.size(); j++) { // 判断我的坦克是否与砖块重叠
						if (robot1.Overlap(bricks.get(j)) == true) {
							robot1.setCrash(true);
							break;
						}
					}
					for (int j = 0; j < irons.size(); j++) { // 判断我的坦克是否与铁块重叠
						if (robot1.Overlap(irons.get(j)) == true) {
							robot1.setCrash(true);
							break;
						}
					}
					for (int j = 0; j < waters.size(); j++) { // 判断我的坦克是否与河流重叠
						if (robot1.Overlap(waters.get(j)) == true) {
							robot1.setCrash(true);
							break;
						}
					}
					if (robot1.isCrash()) { // 如果坦克在该坐标，该方向的下一个扩展点会发生碰撞，则该扩展点无效
						continue;
					} else {
						newPosition.setDirect(direct);
						newPosition.setLast(p);
						newPosition.setG(p.getG() + speed);
						newPosition.setH(h(newPosition, endPosition));
						newPosition.setF(newPosition.getH() + newPosition.getG());
						opentable.add(newPosition); // 最小的耗散值的节点进入open表
					}
				}
			}
		}
		LinkedList<Tank> list = new LinkedList<>();
		while (lastPosition != null) {
			Tank t = new Tank(lastPosition.getX(), lastPosition.getY(), lastPosition.getDirect(),
					startTank.getTankId());
			list.addFirst(t);
			lastPosition = lastPosition.getLast();
		}
		if (list.size() >= 1) { // 第一个点是起始点的坐标，删除
			list.removeFirst();
		}
		return list;
	}

	/**
	 * 执行规划的路径，如果在执行过程中，碰撞了其他坦克，不执行剩下的路径，该路径作废，返回true
	 *
	 * @param routes
	 * @param step
	 * @param robot
	 * @return
	 */
	private boolean drawRoute(LinkedList<Tank> routes, int step, RobotTank robot) {
		Iterator<Tank> it = routes.iterator();
		while (it.hasNext() && step >= 0) {
			Tank route = it.next();
			int robotSize = resource.getRobotTanks().size();
			try {
				Thread.sleep(45 / robotSize); // 控制每个坦克隔45毫秒走一步
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ArithmeticException e) {
				return false;
			}
			// 判断：如果沿着规划的路线继续走，会不会跑出警戒范围，如果会，则不走规划的路线
			if (hEuclidianDistance(robot.getCircleCenterX(), robot.getCircleCenterY(), route.getX(),
					route.getY()) > RobotTank.WARNRANGE) {
				return true;
			}
			boolean crash = false; // 机器坦克在规划的路线上发生碰撞
			for (Tank t : resource.getRobotTanks().values()) { // 判断机器坦克是否与其他机器坦克重叠
				if (!robot.getTankId().equals(t.getTankId())) {
					if (route.Overlap(t) && t.isAlive()) { // 有可能坦克死了，但是子弹仍然显示在界面上，坦克未被移除
						crash = true;
						break;
					}
				}
			}
			for (Tank t : resource.getPlayerTanks().values()) { // 判断机器坦克是否与玩家坦克重叠
				if (route.Overlap(t) && t.isAlive()) { // 有可能坦克死了，但是子弹仍然显示在界面上，坦克未被移除
					crash = true;
					break;
				}
			}
			if (crash == true) { // 在路径上碰撞的话，不再继续走
				return true;
			}
			robot.setX(route.getX());
			robot.setY(route.getY());
			robot.setDirect(route.getDirect());
			it.remove();// 删除该遍历过的位置
			step--; // 剩余步数减1


			for (Tank t : resource.getPlayerTanks().values()) {
				if (Math.abs(t.getX() - robot.getX()) < robot.getWidth() / 2 && t.getY() < robot.getY()) { // 如果机器坦克在下面
					boolean ironFlag = false; // 路径中是否有钢铁的标识
					for (Iron iron : resource.getIrons()) { // 子弹路径上，不会有钢铁
						if (Math.abs(iron.getX() - robot.getX()) < iron.getWidth() / 2 && iron.getY() < robot.getY()
								&& iron.getY() > t.getY()) {
							ironFlag = true;
							break;
						}
					}
					if (ironFlag == false) {
						synchronized (robot.getRemainBulletCount()) {
							if (robot.getRemainBulletCount() > 0) {
								robot.setDirect(Tank.NORTH);
								Bullet bullet = robot.shot();
								Thread thread = new Thread(new BulletRepaintThread(bullet, robot,resource,tankGame));
								thread.start();
							}
						}

					}
				} else if (Math.abs(t.getX() - robot.getX()) < robot.getWidth() / 2 && t.getY() > robot.getY()) { // 如果机器坦克在上面
					boolean ironFlag = false; // 路径中是否有钢铁的标识
					for (Iron iron : resource.getIrons()) { // 子弹路径上，不会有钢铁
						if (Math.abs(iron.getX() - robot.getX()) < iron.getWidth() / 2 && iron.getY() > robot.getY()
								&& iron.getY() < t.getY()) {
							ironFlag = true;
							break;
						}
					}
					if (ironFlag == false) {
						synchronized (robot.getRemainBulletCount()) {
							if (robot.getRemainBulletCount() > 0) {
								robot.setDirect(Tank.SOUTH);
								Bullet bullet = robot.shot();
								Thread thread = new Thread(new BulletRepaintThread(bullet, robot,resource,tankGame));
								thread.start();
							}
						}
					}
				} else if (Math.abs(t.getY() - robot.getY()) < robot.getWidth() / 2 && t.getX() > robot.getX()) { // 如果机器坦克在左边
					boolean ironFlag = false; // 路径中是否有钢铁的标识
					for (Iron iron : resource.getIrons()) { // 子弹路径上，不会有钢铁
						if (Math.abs(iron.getY() - robot.getY()) < iron.getWidth() / 2 && iron.getX() > robot.getX()
								&& iron.getX() < t.getX()) {
							ironFlag = true;
							break;
						}
					}
					if (ironFlag == false) {
						synchronized (robot.getRemainBulletCount()) {
							if (robot.getRemainBulletCount() > 0) {
								robot.setDirect(Tank.EAST);
								Bullet bullet = robot.shot();
								Thread thread = new Thread(new BulletRepaintThread(bullet, robot,resource,tankGame));
								thread.start();
							}
						}
					}
				} else if (Math.abs(t.getY() - robot.getY()) < robot.getWidth() / 2 && t.getX() < robot.getX()) { // 如果机器坦克在右边
					boolean ironFlag = false; // 路径中是否有钢铁的标识
					for (Iron iron : resource.getIrons()) { // 子弹路径上，不会有钢铁
						if (Math.abs(iron.getY() - robot.getY()) < iron.getWidth() / 2 && iron.getX() < robot.getX()
								&& iron.getX() > t.getX()) {
							ironFlag = true;
							break;
						}
					}
					if (ironFlag == false) {
						synchronized (robot.getRemainBulletCount()) {
							if (robot.getRemainBulletCount() > 0) {
								robot.setDirect(Tank.WEST);
								Bullet bullet = robot.shot();
								Thread thread = new Thread(new BulletRepaintThread(bullet, robot,resource,tankGame));
								thread.start();
							}
						}
					}
				}
			}
			// 发送位置信息
            TankLocationResponse tankLocationResponse = new TankLocationResponse();
            tankLocationResponse.setTankId(robot.getTankId());
            tankLocationResponse.setX(robot.getX());
            tankLocationResponse.setY(robot.getY());
            tankLocationResponse.setDirect(robot.getDirect());
            tankGame.sendMsg(TankGameDTO.MsgType.REFRESH_TANK,tankLocationResponse);
		}
		return false;
	}

	double h(Position pnt, Position endPosition) {
		return hEuclidianDistance(pnt, endPosition);
	}

	/**
	 * 欧式距离,小于等于实际值
	 */
	static double hEuclidianDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * 欧式距离,小于等于实际值
	 */
	double hEuclidianDistance(Position pnt, Position endPosition) {
		return Math.sqrt(Math.pow(pnt.getX() - endPosition.getX(), 2) + Math.pow(pnt.getY() - endPosition.getY(), 2));
	}
}
