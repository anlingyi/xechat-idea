package cn.xeblog.plugin.game.tank.entity;

public class MapEntity {
	private int x;
	private int y;
	private int type;
	private int mapId;
	public static final int BRICK=1;
	public static final int WATER=2;
	public static final int IRON=3;
	public static final int ROBOT=4;
	public static final int PLAYER_TANK=5;

	public MapEntity(int x, int y, int type, int mapId) {
		this.x = x;
		this.y = y;
		this.type = type;
		this.mapId = mapId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

}
