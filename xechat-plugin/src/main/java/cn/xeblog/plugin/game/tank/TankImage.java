package cn.xeblog.plugin.game.tank;

import cn.hutool.core.io.IoUtil;

import java.awt.*;

/**
 * 坦克元素加载
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class TankImage {

	private static Toolkit tk = Toolkit.getDefaultToolkit();
	/**
	 * 我的坦克四个方向图像数组
	 */
	public static Image myTankImg[] = {
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/UTank_.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/DTank_.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/LTank_.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/RTank_.gif")))};

	/**
	 * 敌方坦克四个方向图像数组
	 */
	public static Image enemyTankImg[] = {
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/UTank.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/DTank.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/LTank.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/RTank.gif")))};


	/**
	 * 障碍物图像数组
	 */
	public static Image stuffImg[] = {
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/brick.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/iron.gif"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/water.gif"))) };

	/**
	 * 子弹图像
	 */
	public static Image bullet = tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bullet.gif")));

	/**
	 * 爆炸图像
	 */
	public static Image bomb[] = {
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bomb_1.png"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bomb_2.png"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bomb_3.png"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bomb_4.png"))),
			tk.createImage(IoUtil.readBytes(TankImage.class.getClassLoader().getResourceAsStream("images/bomb_5.png"))) };

}
