package cn.xeblog.plugin.game.tank.msg;


import java.io.Serializable;

/**
 * 坦克移动的信息
 * 
 * @author young
 *
 */
public class TankLocationResponse implements Serializable {
	private Integer x;
	private Integer y;
	private String tankId;
	private Integer direct;


	public TankLocationResponse() {

	}

	public Integer getX() {
		return x;
	}

	public void setX(Integer x) {
		this.x = x;
	}

	public Integer getY() {
		return y;
	}

	public void setY(Integer y) {
		this.y = y;
	}

	public String getTankId() {
		return tankId;
	}

	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

	public Integer getDirect() {
		return direct;
	}

	public void setDirect(Integer direct) {
		this.direct = direct;
	}

}
