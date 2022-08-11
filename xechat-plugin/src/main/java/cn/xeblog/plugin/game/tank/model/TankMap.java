package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * 场景地图
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class TankMap implements Serializable{
    private CopyOnWriteArrayList<Brick> bricks;
    private CopyOnWriteArrayList<Iron> irons;
    private CopyOnWriteArrayList<Water> waters;

    public TankMap() {
        bricks = new CopyOnWriteArrayList<Brick>();
        irons = new CopyOnWriteArrayList<Iron>();
        waters = new CopyOnWriteArrayList<Water>();
    }

    public CopyOnWriteArrayList<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(CopyOnWriteArrayList<Brick> bricks) {
        this.bricks = bricks;
    }

    public CopyOnWriteArrayList<Iron> getIrons() {
        return irons;
    }

    public void setIrons(CopyOnWriteArrayList<Iron> irons) {
        this.irons = irons;
    }

    public CopyOnWriteArrayList<Water> getWaters() {
        return waters;
    }

    public void setWaters(CopyOnWriteArrayList<Water> waters) {
        this.waters = waters;
    }
}
