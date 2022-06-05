package cn.xeblog.plugin.game.landlords;

import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;

import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/3 3:19 下午
 */
public class AIPlayerAction extends PlayerAction {

    public AIPlayerAction(List<Poker> pokers, int role, int nextRole) {
        super(pokers, role, nextRole);
    }

    @Override
    public int callScore(int score) {
        return 0;
    }

    @Override
    public PokerInfo processOutPoker(int role, PokerInfo pokerInfo) {
        return null;
    }

}
