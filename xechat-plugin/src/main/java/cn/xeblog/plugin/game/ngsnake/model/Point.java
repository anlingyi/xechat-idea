package cn.xeblog.plugin.game.ngsnake.model;

import java.util.Objects;

/**
 * 坐标
 *
 * @author anlingyi
 * @date 2022/8/2 3:35 PM
 */
public class Point {

    public int x;

    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
