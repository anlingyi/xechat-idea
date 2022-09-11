package cn.xeblog.commons.entity.game.chess;

import cn.xeblog.commons.entity.game.GameDTO;
import lombok.*;

/**
 * @author anlingyi
 * @date 2020/6/5
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChessDTO extends GameDTO {

    private int x;
    private int y;

    /** 对战方式：1-红棋 2-黑棋 */
    private int type;

    /** 棋子索引 */
    private int index;

    /** 选项 */
    Option option = Option.DEFAULT;

    /** 当前界面 用途：后期可设置我方和对方不同界面 */
    public UI currentUI = UI.CLASSIC;

    public enum Option {
        SURRENDER, UNDO, GAME_OVER, CHECK, DEFAULT
    }

    @Getter
    @AllArgsConstructor
    public enum UI {
        CLASSIC("经典模式", 1)
//        , FISH("摸鱼模式", 0)

        ;

        private String name;

        private int value;
    }

}
