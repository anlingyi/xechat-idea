package cn.xeblog.plugin.game.tank.msg;

import java.io.Serializable;

public class BombResponse implements Serializable {
	private int x;
	private int y;
	private int width;
	private String tankId; // 产生爆炸的子弹的坦克id
	private String bulletId; // 子弹id

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getTankId() {
		return tankId;
	}

	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

	public String getBulletId() {
		return bulletId;
	}

	public void setBulletId(String bulletId) {
		this.bulletId = bulletId;
	}

}
