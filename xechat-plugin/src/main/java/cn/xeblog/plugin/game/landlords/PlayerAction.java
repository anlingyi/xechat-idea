package cn.xeblog.plugin.game.landlords;

import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;

import java.util.*;

/**
 * @author anlingyi
 * @date 2022/6/3 2:38 下午
 */
public abstract class PlayerAction {

    /**
     * 手牌
     */
    protected List<Poker> pokers;

    /**
     * 玩家节点
     */
    protected PlayerNode playerNode;

    public PlayerAction(PlayerNode playerNode) {
        this.playerNode = playerNode;
    }

    public void setPokers(List<Poker> pokers) {
        this.pokers = pokers;
    }

    public void setLastPokers(List<Poker> lastPokers) {
        if (this.pokers == null) {
            return;
        }

        this.pokers.addAll(lastPokers);
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
     * @param outPlayer 出牌人
     * @param pokerInfo 出牌信息
     * @return null表示不出牌
     */
    protected PokerInfo outPoker(PlayerNode outPlayer, PokerInfo pokerInfo) {
        if (outPlayer != null && outPlayer == playerNode) {
            outPlayer = null;
            pokerInfo = null;
        }

        PokerInfo out = processOutPoker(outPlayer, pokerInfo);
        if (out == null) {
            return null;
        }

        this.pokers.removeAll(out.getPokers());

        return out;
    }

    /**
     * 出牌处理
     *
     * @param outPlayer 出牌人
     * @param pokerInfo 出牌信息
     * @return null表示不出牌
     */
    public abstract PokerInfo processOutPoker(PlayerNode outPlayer, PokerInfo pokerInfo);

}
