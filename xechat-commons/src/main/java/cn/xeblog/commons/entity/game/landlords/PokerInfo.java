package cn.xeblog.commons.entity.game.landlords;

import lombok.Data;

import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/2 3:27 下午
 */
@Data
public class PokerInfo {

    /**
     * 扑克牌列表
     */
    private List<Poker> pokers;

    /**
     * 牌型
     */
    private PokerModel pokerModel;

    /**
     * 比较值
     */
    private int value;

    /**
     * 当前牌型是否比它大
     *
     * @param pokerInfo
     * @return
     */
    public boolean biggerThanIt(PokerInfo pokerInfo) {
        if (pokerInfo == null) {
            return true;
        }

        PokerModel itsPokerModel = pokerInfo.getPokerModel();
        if (this.pokerModel == PokerModel.ROCKET) {
            // 我是火箭，我大
            return true;
        }
        if (itsPokerModel == PokerModel.ROCKET) {
            // 它是火箭，它大
            return false;
        }
        if (this.pokerModel == pokerInfo.getPokerModel()) {
            // 牌型一样，牌数相同，我的比较值大就大它
            return this.value > pokerInfo.getValue() && this.pokers.size() == pokerInfo.getPokers().size();
        }

        // 我是炸弹就大它
        return this.pokerModel == PokerModel.BOMB;
    }

}
