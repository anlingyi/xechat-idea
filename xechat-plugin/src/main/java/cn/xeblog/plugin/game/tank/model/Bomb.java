package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;

/**
 * 炸弹
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Bomb extends Element implements Serializable {

	public Bomb(Integer x, Integer y) {
		super(x, y);
	}

	/**
	 * 炸弹的生命值，在画面中展示的时间长短
	 */
	private Integer lifeValue = 30;

	public void lifeDown() {
		if (lifeValue > 0){
			lifeValue-=5;
		}
	}

	public Integer getLifeValue() {
		return lifeValue;
	}

	public void setLifeValue(Integer lifeValue) {
		this.lifeValue = lifeValue;
	}

}
