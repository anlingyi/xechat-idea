package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;

/**
 *  砖
 *@author: SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Brick extends Element implements Serializable {

    public Brick(Integer x, Integer y) {
        super(x, y);
        this.setType(BRICK);
        this.setWidth(20);
        this.setHeight(20);
    }

}
