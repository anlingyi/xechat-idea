package cn.xeblog.plugin.game.chess;

import cn.xeblog.commons.entity.game.chess.ChessDTO;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 棋子点位
 *
 * @author anlingyi
 * @date 2021/11/7 5:59 下午
 */
@ToString
@NoArgsConstructor
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
     * 棋子类型 255.红 0.黑
     */
    int type;

    /** 棋子索引 */
    int index;

    /** 选项 */
    ChessDTO.Option option = ChessDTO.Option.DEFAULT;

    public Point(int x, int y, int type, int index) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.index = index;
    }

    public Point(int x, int y, int type, int index, ChessDTO.Option option) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.index = index;
        this.option = option;
    }

    public Point(ChessDTO.Option option) {
        this.option = option;
    }
}
