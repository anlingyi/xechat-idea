package cn.xeblog.plugin.game.tank.msg;

import java.io.Serializable;

public class BulletLocationResponse implements Serializable {

	private int x;
	private int y;
	private String bulletId;
	private String tankId;

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

	public String getBulletId() {
		return bulletId;
	}

	public void setBulletId(String bulletId) {
		this.bulletId = bulletId;
	}

	public String getTankId() {
		return tankId;
	}

	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

}
