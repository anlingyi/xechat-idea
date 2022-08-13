package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;


/**
 *  子弹
 *@author: SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Bullet extends Element implements Serializable{

	// 子弹运行方向
	private int movingDirect;
	// 子弹运行速度
	private int speed = 4;

	private String bulletId;
	//子弹射程
	public static final int RANGE=400;

	//子弹爆炸的宽度
	public static final int EXPLOSIONG_RANGE=20;

	public Bullet(Integer x, Integer y, Integer direct) {
		super(x, y);
		// 设置子弹的运行方向
		this.setMovingDirect(direct);
	}

	public int getMovingDirect() {
		return movingDirect;
	}

	public void setMovingDirect(int movingDirect) {
		this.movingDirect = movingDirect;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	public String getBulletId() {
		return bulletId;
	}

	public void setBulletId(String bulletId) {
		this.bulletId = bulletId;
	}

}
