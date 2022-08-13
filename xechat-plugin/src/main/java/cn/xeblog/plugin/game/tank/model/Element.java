package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;


/**
 * 基础元素
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Element implements Serializable{
	/**
	 * 物体的x坐标
	 */
	private Integer x;
	/**
	 * 物体的y坐标
	 */
	private Integer y;
	/**
	 * 物体的宽度
	 */
	private Integer width;
	/**
	 * 物体的高度
	 */
	private Integer height;
	/**
	 * 方向
	 */
	private Integer direct;
	/**
	 * 元素类型
	 */
	private int type;
	/**
	 * 方向北
	 */
	public static final int NORTH = 0;
	/**
	 * 方向南
	 */
	public static final int SOUTH = 1;
	/**
	 * 方向西
	 */
	public static final int WEST = 2;
	/**
	 * 方向东
	 */
	public static final int EAST = 3;
	/**
	 * 砖块
	 */
	public static final int BRICK = 0;
	/**
	 * 铁块
	 */
	public static final int IRON = 1;
	/**
	 * 水池
	 */
	public static final int WATER = 2;
	/**
	 * 坦克
	 */
	public static final int TANK = 7;
	/**
	 * 地图
	 */
	public static final Integer MAP = 8;
	/**
	 * 该元素是否应该展示在画面上
	 */
	private boolean isAlive;

	public Element(Integer x, Integer y) {
		this.x = x;
		this.y = y;
		this.isAlive=true;
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

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public boolean isAlive() {
		return isAlive;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(Integer direct) {
		this.direct = direct;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
