package cn.xeblog.plugin.game.ngsnake.model;

import java.util.List;

/**
 * 蛇
 *
 * @author anlingyi
 * @date 2022/8/2 3:32 PM
 */
public class Snake {

    public static int width = 10;

    public static int height = 10;

    /**
     * 蛇身坐标列表
     */
    public List<Point> body;

    public Snake(List<Point> body) {
        this.body = body;
    }

    /**
     * 添加蛇身坐标
     *
     * @param x
     * @param y
     */
    public void add(int x, int y) {
        this.body.add(new Point(x * width, y * height));
    }

    /**
     * 移除蛇尾坐标
     */
    public void removeLast() {
        int size = size();
        if (size == 0) {
            return;
        }

        this.body.remove(size - 1);
    }

    /**
     * 获取蛇头坐标
     *
     * @return
     */
    public Point getHead() {
        if (size() > 0) {
            return this.body.get(0);
        }

        return null;
    }

    /**
     * 获取蛇尾坐标
     *
     * @return
     */
    public Point getTail() {
        int size = size();
        if (size > 0) {
            return this.body.get(size - 1);
        }

        return null;
    }

    /**
     * 蛇身长度
     *
     * @return
     */
    public int size() {
        return this.body.size();
    }

    /**
     * 蛇移动
     *
     * @param direction 移动方向
     */
    public void move(Direction direction) {
        if (size() == 0) {
            return;
        }

        for (int i = this.size() - 1; i > 0; i--) {
            // 从蛇尾开始向前移动
            Point point = this.body.get(i);
            Point nextPoint = this.body.get(i - 1);
            point.x = nextPoint.x;
            point.y = nextPoint.y;
        }

        // 蛇头移动
        Point head = getHead();
        switch (direction) {
            case UP:
                head.y -= height;
                break;
            case DOWN:
                head.y += height;
                break;
            case LEFT:
                head.x -= width;
                break;
            case RIGHT:
                head.x += width;
                break;
        }
    }

}
