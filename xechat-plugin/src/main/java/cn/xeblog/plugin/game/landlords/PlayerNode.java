package cn.xeblog.plugin.game.landlords;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2022/6/3 4:27 下午
 */
@Data
@NoArgsConstructor
public class PlayerNode {

    /**
     * 当前玩家
     */
    private String player;

    /**
     * 手牌数
     */
    private int pokerTotal;

    /**
     * 角色 1.农民 2.地主
     */
    private int role;

    /**
     * 前一位玩家
     */
    private PlayerNode prevPlayer;

    /**
     * 后一位玩家
     */
    private PlayerNode nextPlayer;

    public PlayerNode(String player) {
        this.player = player;
    }

    public int minusPoker(int total) {
        this.pokerTotal -= total;
        return this.pokerTotal;
    }

    @Override
    public String toString() {
        return "PlayerNode{" +
                "player='" + player + '\'' +
                ", pokerTotal=" + pokerTotal +
                ", role=" + role +
                '}';
    }

}
