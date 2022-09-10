package cn.xeblog.plugin.game.box.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 功能描述: 人物位置信息
 *
 * @author ☆程序员鼓励师☆
 * @date 2022年8月20日01:02:27
 */
@Getter
@AllArgsConstructor
public class CustomMap {
    int manX;
    int manY;
    int[][] map;
}

