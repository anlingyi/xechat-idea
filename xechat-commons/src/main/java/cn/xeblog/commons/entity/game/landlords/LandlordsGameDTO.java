package cn.xeblog.commons.entity.game.landlords;

import cn.xeblog.commons.entity.game.GameDTO;
import lombok.Data;

/**
 * @author anlingyi
 * @date 2022/6/2 1:14 下午
 */
@Data
public class LandlordsGameDTO extends GameDTO {

    /**
     * 消息类型
     */
    private MsgType msgType;

    /**
     * 玩家昵称
     */
    private String player;

    /**
     * 数据内容
     */
    private Object data;

    public enum MsgType {
        /**
         * 分牌
         */
        ALLOC_POKER,
        /**
         * 叫分
         */
        CALL_SCORE,
        /**
         * 出牌
         */
        OUT_POKER,
        /**
         * 游戏结束
         */
        GAME_OVER
    }

}
