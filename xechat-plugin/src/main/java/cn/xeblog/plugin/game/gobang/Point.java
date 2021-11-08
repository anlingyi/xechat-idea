package cn.xeblog.plugin.game.gobang;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 棋子点位
 *
 * @author anlingyi
 * @date 2021/11/7 5:59 下午
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Point {
    /**
     * 横坐标
     */
    int x;
    /**
     * 纵坐标
     */
    int y;
    /**
     * 棋子类型 1.黑 2.白
     */
    int type;
}
