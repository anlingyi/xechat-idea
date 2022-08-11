package cn.xeblog.plugin.game.tank.msg;

import java.io.Serializable;

/**
 * 坦克被击中的消息
 * 
 * @author Administrator
 *
 */
public class DestroyTankResponse implements Serializable {
	
	public DestroyTankResponse(String tankId) {
		this.tankId = tankId;
	}

	private String tankId;

	public String getTankId() {
		return tankId;
	}

	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

}
