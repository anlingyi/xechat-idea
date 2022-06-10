package cn.xeblog.plugin.game.landlords;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;
import cn.xeblog.commons.entity.game.landlords.PokerModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anlingyi
 * @date 2022/6/3 3:19 下午
 */
public class AIPlayerAction extends PlayerAction {

    private Map<PokerModel, List<PokerInfo>> pokerModelMap;

    public AIPlayerAction(PlayerNode playerNode) {
        super(playerNode);
    }

    @Override
    public int callScore(int score) {
        int call = 0;
        if (pokerModelMap.containsKey(PokerModel.ROCKET) || pokerModelMap.containsKey(PokerModel.BOMB)) {
            call = 3;
        }
        return call;
    }

    @Override
    public PokerInfo processOutPoker(PlayerNode outPlayer, PokerInfo pokerInfo) {
        PokerInfo out = null;
        if (pokerInfo != null) {
            List<PokerInfo> pokerInfoList = pokerModelMap.get(pokerInfo.getPokerModel());
            if (pokerInfoList != null) {
                for (PokerInfo info : pokerInfoList) {
                    if (info.biggerThanIt(pokerInfo)) {
                        out = info;
                        break;
                    }
                }
            } else {
                List<PokerInfo> bombList = pokerModelMap.get(PokerModel.BOMB);
                if (bombList != null) {
                    if (pokerInfo.getPokerModel() != PokerModel.BOMB) {
                        out = bombList.get(0);
                    } else {
                        for (PokerInfo info : bombList) {
                            if (info.biggerThanIt(pokerInfo)) {
                                out = info;
                                break;
                            }
                        }
                    }
                }
                if (out == null) {
                    List<PokerInfo> rocketList = pokerModelMap.get(PokerModel.ROCKET);
                    if (rocketList != null) {
                        out = rocketList.get(0);
                    }
                }
            }
        } else {
            List<PokerModel> pokerModels = new ArrayList<>(pokerModelMap.keySet());
            int total = pokerModels.size();
            int index = RandomUtil.randomInt(0, total);
            for (int i = 0; i < total; i++) {
                if (i == index) {
                    out = pokerModelMap.get(pokerModels.get(i)).get(0);
                    break;
                }
            }
        }

        if (out != null) {
            List<PokerInfo> outPokerInfoList = pokerModelMap.get(out.getPokerModel());
            outPokerInfoList.remove(out);
            if (outPokerInfoList.size() == 0) {
                pokerModelMap.remove(out.getPokerModel());
            }
        }

        return out;
    }

    @Override
    public void setPokers(List<Poker> pokers) {
        super.setPokers(pokers);
        this.pokerModelMap = buildPokerModel(pokers);
    }

    @Override
    public void setLastPokers(List<Poker> lastPokers) {
        super.setLastPokers(lastPokers);
        this.pokerModelMap = buildPokerModel(super.pokers);
    }

    protected Map<PokerModel, List<PokerInfo>> buildPokerModel(List<Poker> pokers) {
        Map<PokerModel, List<PokerInfo>> pokerModelMap = new HashMap<>();

        Map<Integer, List<Poker>> pokersMap = pokers.stream().collect(Collectors.groupingBy(Poker::getValue));
        boolean hasRocket = false;
        List<Poker> king = pokersMap.get(16);
        List<Poker> bigKing = pokersMap.get(17);
        if (king != null && bigKing != null) {
            hasRocket = true;
            king.addAll(bigKing);
            PokerInfo rocket = PokerUtil.getPokerInfo(king);
            pokerModelMap.put(rocket.getPokerModel(), ListUtil.toList(rocket));
        }

        Set<Integer> keys = pokersMap.keySet();
        for (Integer key : keys) {
            if (hasRocket && key > 15) {
                continue;
            }

            List<Poker> pokerList = pokersMap.get(key);
            PokerInfo pokerInfo = PokerUtil.getPokerInfo(pokerList);
            if (pokerInfo != null) {
                List<PokerInfo> pokerInfoList = pokerModelMap.get(pokerInfo.getPokerModel());
                if (pokerInfoList == null) {
                    pokerInfoList = new ArrayList<>();
                    pokerModelMap.put(pokerInfo.getPokerModel(), pokerInfoList);
                }
                pokerInfoList.add(pokerInfo);
            }
        }

        return pokerModelMap;
    }

}
