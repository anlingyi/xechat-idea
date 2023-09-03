package cn.xeblog.commons.entity.game;

import cn.xeblog.commons.enums.Game;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2022/5/25 3:19 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomMsgDTO extends GameDTO {

    private MsgType msgType;
    private Object content;

    public GameRoomMsgDTO(String roomId, Game game, MsgType msgType, Object content) {
        super(roomId, game);
        this.msgType = msgType;
        this.content = content;
    }

    public enum MsgType {
        /**
         * 邀请玩家
         */
        PLAYER_INVITE,
        /**
         * 邀请玩家结果
         */
        PLAYER_INVITE_RESULT,
        /**
         * 玩家离开房间
         */
        PLAYER_LEFT,
        /**
         * 玩家准备
         */
        PLAYER_READY,
        /**
         * 玩家取消准备
         */
        PLAYER_CANCEL_READY,
        /**
         * 游戏开始
         */
        GAME_START,
        /**
         * 玩家已开始游戏
         */
        PLAYER_GAME_STARTED,
        /**
         * 游戏结束
         */
        GAME_OVER,
        /**
         * 游戏异常
         */
        GAME_ERROR,
        /**
         * 房间关闭
         */
        ROOM_CLOSE;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = MsgType.valueOf(msgType);
    }

}
