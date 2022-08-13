package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;

/**
 * 铁块
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Iron extends Element implements Serializable{

	public Iron(Integer x, Integer y) {
		super(x, y);
		this.setType(IRON);
		this.setWidth(20);
		this.setHeight(20);
	}

}
