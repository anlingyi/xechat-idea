package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;

/**
 * 地图的描述信息
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Chapter implements Serializable {

    private int id;
    private String name;

    public Chapter(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
