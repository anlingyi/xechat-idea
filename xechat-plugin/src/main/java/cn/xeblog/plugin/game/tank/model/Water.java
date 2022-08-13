package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;

/**
 * 水池
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Water extends Element implements Serializable{

	public Water(Integer x, Integer y) {
		super(x, y);
		this.setType(WATER);
		this.setWidth(20);
		this.setHeight(20);
	}

}
