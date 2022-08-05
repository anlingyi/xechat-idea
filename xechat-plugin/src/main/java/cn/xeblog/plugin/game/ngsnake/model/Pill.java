package cn.xeblog.plugin.game.ngsnake.model;

/**
 * 药丸
 *
 * @author anlingyi
 * @date 2022/8/2 4:49 PM
 */
public class Pill {

    public static int width = 10;

    public static int height = 10;

    /**
     * 坐标
     */
    public Point point;

    /**
     * 药丸类型
     */
    public PillType pillType;

    public enum PillType {
        /**
         * 红色药丸
         */
        RED(5),
        /**
         * 蓝色药丸
         */
        BLUE(2),
        /**
         * 绿色药丸
         */
        GREEN(1),
        ;

        /**
         * 分数
         */
        public int score;

        PillType(int score) {
            this.score = score;
        }
    }

    public Pill(int x, int y, PillType pillType) {
        this.point = new Point(x * width, y * height);
        this.pillType = pillType;
    }

}
