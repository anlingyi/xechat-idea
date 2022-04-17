package cn.xeblog.plugin.game.gobang;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 棋子点位
 *
 * @author anlingyi
 * @date 2021/11/7 5:59 下午
 */
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
    /**
     * 得分
     */
    int score;

    public Point(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
}
