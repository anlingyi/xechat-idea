package cn.xeblog.plugin.game.landlords;

import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;
import lombok.Getter;

import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/3 2:38 下午
 */
public abstract class PlayerAction {

    /**
     * 手牌
     */
    @Getter
    private List<Poker> pokers;

    /**
     * 角色，1.农民 2.地主
     */
    @Getter
    private int role;

    /**
     * 后一位玩家角色
     */
    private int nextRole;

    public PlayerAction(List<Poker> pokers, int role, int nextRole) {
        this.pokers = pokers;
        this.role = role;
        this.nextRole = nextRole;
    }

    /**
     * 叫分
     *
     * @param score 上一玩家叫分
     * @return 0~3分
     */
    public abstract int callScore(int score);

    /**
     * 出牌
     *
     * @param role      出牌人角色 1.农民 2.地主
     * @param pokerInfo 出牌人角色 1.农民 2.地主
     * @return null表示不出牌
     */
    protected PokerInfo outPoker(int role, PokerInfo pokerInfo) {
        PokerInfo out = processOutPoker(role, pokerInfo);
        if (out == null) {
            return null;
        }

        this.pokers.removeAll(out.getPokers());
        return out;
    }

    /**
     * 出牌处理
     *
     * @param role      出牌人角色 1.农民 2.地主
     * @param pokerInfo 出牌人角色 1.农民 2.地主
     * @return null表示不出牌
     */
    public abstract PokerInfo processOutPoker(int role, PokerInfo pokerInfo);

}
