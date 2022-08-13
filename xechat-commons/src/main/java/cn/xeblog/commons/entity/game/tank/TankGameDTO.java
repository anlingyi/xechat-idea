package cn.xeblog.commons.entity.game.tank;

import cn.xeblog.commons.entity.game.GameDTO;
import cn.xeblog.commons.enums.Game;

/**
 * 坦克游戏房间
 *@author : SunYb
 *@date: 2022/8/5 14:13
 *@version: 1.0
 */
public class TankGameDTO extends GameDTO {

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
         * 制造炸弹
         */
        CREATE_BOMB,
        /**
         * 刷新子弹位置
         */
        REFRESH_BULLET,
        /**
         * 删除被击毁的砖块
         */
        REMOVE_BRICK,
        /**
         * 刷新坦克的位置
         */
        REFRESH_TANK,
        /**
         * 坦克被击毁
         */
        DESTROY_TANK,
    }

    public TankGameDTO() {
    }

    public TankGameDTO(MsgType msgType, String player, Object data) {
        this.msgType = msgType;
        this.player = player;
        this.data = data;
    }

    public TankGameDTO(String roomId, Game game, MsgType msgType, String player, Object data) {
        super(roomId, game);
        this.msgType = msgType;
        this.player = player;
        this.data = data;
    }

    public MsgType getMsgType() {
        return msgType;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
