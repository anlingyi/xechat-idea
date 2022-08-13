package cn.xeblog.plugin.game.tank.model;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 坦克基本信息
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class Tank extends Element implements Serializable{

	private HashMap<String, Bullet> bullets=new HashMap<>();
	private Integer speed = 4;
	private Integer direct;
	// 是否碰撞
	private boolean isCrash;
	//每个坦克有唯一的id，对不同玩家的坦克进行区分
	private String tankId;
	private Integer remainBulletCount=3;	//剩余的子弹的数量

	/**
	 * 坦克往北走
	 */
	public void goNorth() {
		this.setDirect(NORTH);
		if (this.getY() > 20) {
			this.setY(this.getY() - this.speed);
		}
	}

	/**
	 * 坦克往南走
	 */
	public void goSouth(int panelHeight) {
		this.setDirect(SOUTH);
		if (this.getY() < panelHeight-24) {
			this.setY(this.getY() + this.speed);
		}
	}

	/**
	 * 坦克往西走
	 */
	public void goWest(int panelHeight) {
		this.setDirect(WEST);
		if (this.getX() > 20 && this.getY() <= panelHeight - 20) {
			this.setX(this.getX() - this.speed);
		}
	}

	/**
	 * 坦克往东走
	 */
	public void goEast(int panelHeight,int panelWidth) {
		this.setDirect(EAST);
		if (this.getX() < panelWidth - 20 && this.getY() <= panelHeight - 20) {
			this.setX(this.getX() + this.speed);
		}
	}

	public Tank(Integer x, Integer y, Integer direct, String tankId) {
		super(x, y);
		this.tankId=tankId;
		this.direct = direct;
		this.setType(TANK);
		this.setWidth(40);
		this.setHeight(40);
	}

	/**
	 * 发射子弹
	 * 
	 * @param tank
	 */
	public Bullet shot() {
		Bullet bullet = null;
		switch (this.getDirect()) { // 选择坦克的方向
		case NORTH:
			bullet = new Bullet(this.getX(), this.getY() - 20, NORTH);
			break;
		case SOUTH:
			bullet = new Bullet(this.getX(), this.getY() + 20, SOUTH);
			break;
		case WEST:
			bullet = new Bullet(this.getX() - 20, this.getY(), WEST);
			break;
		case EAST:
			bullet = new Bullet(this.getX() + 20, this.getY(), EAST);
			break;
		}

		return bullet;
	}

	/**
	 * 判断坦克是否与另一个事物重叠
	 */
	public boolean Overlap(Element element) {
		int x = element.getX();
		int y = element.getY();
		if (this.getDirect() == Tank.NORTH) {
			// 先假设该坦克往前移动一步
			this.setY(this.getY() - this.getSpeed());
			if (Math.abs(this.getY() - y) < element.getWidth() / 2 + this.getWidth() / 2
					&& Math.abs(this.getX() - x) < element.getWidth() / 2 + this.getWidth() / 2) {
				this.setY(this.getY() + this.getSpeed());
				return true;
			}
			this.setY(this.getY() + this.getSpeed());
		}
		if (this.getDirect() == Tank.SOUTH) {
			// 先假设该坦克往前移动一步
			this.setY(this.getY() + this.getSpeed());
			if (Math.abs(this.getY() - y) < element.getWidth() / 2 + this.getWidth() / 2
					&& Math.abs(this.getX() - x) < element.getWidth() / 2 + this.getWidth() / 2) {
				this.setY(this.getY() - this.getSpeed());
				return true;
			}
			this.setY(this.getY() - this.getSpeed());
		}
		if (this.getDirect() == Tank.EAST) {
			this.setX(this.getX() + this.getSpeed());
			if (Math.abs(this.getY() - y) < element.getWidth() / 2 + this.getWidth() / 2
					&& Math.abs(this.getX() - x) < element.getWidth() / 2 + this.getWidth() / 2) {
				this.setX(this.getX() - this.getSpeed());
				return true;
			}
			this.setX(this.getX() - this.getSpeed());
		}
		if (this.getDirect() == Tank.WEST) {
			this.setX(this.getX() - this.getSpeed());
			if (Math.abs(this.getY() - y) < element.getWidth() / 2 + this.getWidth() / 2
					&& Math.abs(this.getX() - x) < element.getWidth() / 2 + this.getWidth() / 2) {
				this.setX(this.getX() + this.getSpeed());
				return true;
			}
			this.setX(this.getX() + this.getSpeed());
		}
		return false;
	}


	public HashMap<String, Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(HashMap<String, Bullet> bullets) {
		this.bullets = bullets;
	}
	
	

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	@Override
	public int getDirect() {
		return direct;
	}

	@Override
	public void setDirect(Integer direct) {
		this.direct = direct;
	}

	public boolean isCrash() {
		return isCrash;
	}

	public void setCrash(boolean isCrash) {
		this.isCrash = isCrash;
	}

	public String getTankId() {
		return tankId;
	}

	public void setTankId(String tankId) {
		this.tankId = tankId;
	}

	
	public Integer getRemainBulletCount() {
		return remainBulletCount;
	}

	public void setRemainBulletCount(Integer remainBulletCount) {
		this.remainBulletCount = remainBulletCount;
	}

	public void decreaseRemainBulletCount(){
		this.remainBulletCount--;
	}
	
	public void increseRemainBulletCount(){
		this.remainBulletCount++;
	}
	

}