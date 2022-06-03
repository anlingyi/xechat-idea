package cn.xeblog.plugin.game.landlords;

import cn.hutool.core.collection.CollectionUtil;
import cn.xeblog.commons.entity.game.landlords.Poker;
import cn.xeblog.commons.entity.game.landlords.PokerInfo;
import cn.xeblog.commons.entity.game.landlords.PokerModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anlingyi
 * @date 2022/6/2 2:01 下午
 */
public class PokerUtil {

    /**
     * 生成一副扑克牌
     *
     * @return
     */
    public static List<Poker> genPokers() {
        int total = 54;
        List<Poker> pokers = new ArrayList<>(total);
        for (int i = 3; i < 16; i++) {
            for (Poker.Suits suite : Poker.Suits.values()) {
                pokers.add(new Poker(i, suite));
            }
        }
        pokers.add(new Poker(16, null));
        pokers.add(new Poker(17, null));
        return pokers;
    }

    /**
     * 洗牌
     *
     * @param pokers
     */
    public static void shuffle(List<Poker> pokers) {
        Collections.shuffle(pokers);
        Collections.shuffle(pokers);
    }

    /**
     * 排序
     *
     * @param pokers
     * @param isDesc
     */
    public static void sorted(List<Poker> pokers, boolean isDesc) {
        int after = isDesc ? -1 : 1;
        int before = isDesc ? 1 : -1;
        pokers.sort((p1, p2) -> {
            if (p1.getSort() == p2.getSort()) {
                int val = p1.getValue() - p2.getValue();
                if (val > 0) {
                    return after;
                } else if (val < 0) {
                    return before;
                }

                // 牌值相同，则按花色排序
                if (p1.getSuits().ordinal() > p2.getSuits().ordinal()) {
                    return after;
                }
                return before;
            }

            if (p1.getSort() > p2.getSort()) {
                return before;
            }
            return after;
        });
    }

    /**
     * 分配扑克牌
     *
     * @return
     */
    public static List<List<Poker>> allocPokers() {
        int handTotal = 17;
        List<List<Poker>> list = new ArrayList<>(4);
        List<Poker> deck = genPokers();
        shuffle(deck);
        List<Poker> player1 = new ArrayList<>(handTotal);
        List<Poker> player2 = new ArrayList<>(handTotal);
        List<Poker> player3 = new ArrayList<>(handTotal);
        list.add(player1);
        list.add(player2);
        list.add(player3);

        int index = 0;
        for (int i = 0; i < handTotal; i++) {
            for (int j = 0; j < list.size(); j++) {
                list.get(j).add(deck.get(index++));
            }
        }

        sorted(list.get(0), true);
        sorted(list.get(1), true);
        sorted(list.get(2), true);

        list.add(deck.subList(index, deck.size()));
        return list;
    }

    /**
     * 获取出牌信息
     *
     * @param pokers
     * @return
     */
    public static PokerInfo getPokerInfo(List<Poker> pokers) {
        if (CollectionUtil.isEmpty(pokers)) {
            return null;
        }

        int size = pokers.size();
        PokerInfo pokerInfo = new PokerInfo();
        if (size > 1) {
            if (size > 2) {
                sorted(pokers, false);
            } else if (size == 2) {
                if (pokers.get(0).getValue() + pokers.get(1).getValue() == 33) {
                    // 火箭
                    pokerInfo.setPokers(pokers);
                    pokerInfo.setPokerModel(PokerModel.ROCKET);
                    return pokerInfo;
                }
            }

            Map<Integer, List<Poker>> pokersMap = new HashMap<>();
            pokers.forEach(poker -> {
                List<Poker> pokerList = pokersMap.get(poker.getValue());
                if (pokerList == null) {
                    pokerList = new ArrayList<>();
                    pokersMap.put(poker.getValue(), pokerList);
                }
                pokerList.add(poker);
            });

            int value = 0;
            int maxLen = 0;
            int minLen = 99;
            int shunzi = 1;
            int maxShunzi = 0;
            Set<Integer> keys = pokersMap.keySet();
            List<Poker> maxPokers = null;
            List<Poker> singlePokers = new ArrayList<>();
            List<List<Poker>> pairPokers = new ArrayList<>();

            for (Integer key : keys) {
                List<Poker> pokerList = pokersMap.get(key);
                int pokerSize = pokerList.size();
                int maxPokerSize = maxPokers == null ? 0 : maxPokers.size();
                if (pokerSize >= maxPokerSize && key > value) {
                    value = key;
                }
                if (maxPokerSize < pokerSize) {
                    maxPokers = pokerList;
                    maxLen = pokerSize;
                }
                if (pokerSize < minLen) {
                    minLen = pokerSize;
                }
                if (pokerSize == 1) {
                    singlePokers.addAll(pokerList);
                }
                if (pokerSize == 2) {
                    pairPokers.add(pokerList);
                }
            }

            List<Integer> sortedValues = keys.stream().sorted().collect(Collectors.toList());
            int len = sortedValues.size();
            for (int i = 0; i < len; i++) {
                int currentValue = sortedValues.get(i);
                List<Poker> currentPokerList = pokersMap.get(currentValue);
                if (currentPokerList.size() == maxLen) {
                    currentPokerList.forEach(poker -> poker.setSort(1));
                }

                if (shunzi > maxShunzi) {
                    maxShunzi = shunzi;
                }

                if (i == len - 1) {
                    break;
                }

                int nextValue = sortedValues.get(i + 1);
                List<Poker> nextPokerList = pokersMap.get(nextValue);
                if (currentValue < 15 && nextValue < 15 && currentValue + 1 == nextValue && nextPokerList.size() == currentPokerList.size()) {
                    shunzi++;
                } else {
                    shunzi = 1;
                    maxShunzi = 0;
                }
            }

            sorted(pokers, false);
            pokerInfo.setPokers(pokers);
            pokerInfo.setValue(value);
            if (size == 2 && maxLen == 2) {
                // 对牌
                pokerInfo.setPokerModel(PokerModel.PAIR);
            } else if (size == 3 && maxLen == 3) {
                // 三张牌
                pokerInfo.setPokerModel(PokerModel.THREE);
            } else if (maxLen == 4) {
                if (size == 4) {
                    // 炸弹
                    pokerInfo.setPokerModel(PokerModel.BOMB);
                } else if (size == 6 && minLen == 1) {
                    // 四带二单
                    pokerInfo.setPokerModel(PokerModel.FOUR_TWO_SINGLE);
                } else if (size == 8 && minLen == 2) {
                    // 四带两对
                    pokerInfo.setPokerModel(PokerModel.FOUR_TWO_PAIR);
                } else {
                    return null;
                }
            } else if (size == 4) {
                if (maxLen == 4) {
                    // 炸弹
                    pokerInfo.setPokerModel(PokerModel.BOMB);
                } else if (maxLen == 3) {
                    // 三带一单
                    pokerInfo.setPokerModel(PokerModel.THREE_ONE_SINGLE);
                } else {
                    return null;
                }
            } else if (size == 5 && maxLen == 3 && minLen == 2) {
                // 三带一对
                pokerInfo.setPokerModel(PokerModel.THREE_ONE_PAIR);
            } else if (maxShunzi > 1) {
                if (maxLen - minLen == 0) {
                    if (maxShunzi > 4 && maxLen == 1) {
                        // 单牌顺子
                        pokerInfo.setPokerModel(PokerModel.SHUN_ZI_SINGLE);
                    } else if (maxShunzi > 2 && maxLen == 2) {
                        // 对牌顺子
                        pokerInfo.setPokerModel(PokerModel.SHUN_ZI_PAIR);
                    } else if (maxShunzi > 1 && maxLen == 3) {
                        // 无人飞机
                        pokerInfo.setPokerModel(PokerModel.PLAIN_UNMANNED);
                    } else {
                        return null;
                    }
                } else if (size > 7 && maxShunzi > 1 && maxLen == 3
                        && (singlePokers.size() == maxShunzi && pairPokers.size() == 0 || pairPokers.size() == maxShunzi)) {
                    // 载人飞机
                    pokerInfo.setPokerModel(PokerModel.PLAIN_MANNED);
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            // 单牌
            pokerInfo.setPokers(pokers);
            pokerInfo.setPokerModel(PokerModel.SINGLE);
            pokerInfo.setValue(pokers.get(0).getValue());
        }
        return pokerInfo;
    }

    public static void main(String[] args) {
        // 四带两对
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(15, Poker.Suits.HEART));
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(14, Poker.Suits.CLUB));
//        pokers.add(new Poker(14, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(3, Poker.Suits.HEART));
//        pokers.add(new Poker(15, Poker.Suits.SPADE));
//        testGetPokerInfo(pokers, PokerModel.FOUR_TWO_PAIR);

        // 四带两单
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(14, Poker.Suits.CLUB));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(3, Poker.Suits.HEART));
//        pokers.add(new Poker(15, Poker.Suits.SPADE));
//        testGetPokerInfo(pokers, PokerModel.FOUR_TWO_SINGLE);

        // 载人飞机
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(15, Poker.Suits.CLUB));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(4, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(15, Poker.Suits.SPADE));
//        pokers.add(new Poker(14, Poker.Suits.SPADE));
//        pokers.add(new Poker(14, Poker.Suits.SPADE));
//        pokers.add(new Poker(4, Poker.Suits.SPADE));
//        pokers.add(new Poker(4, Poker.Suits.CLUB));
//        testGetPokerInfo(pokers, PokerModel.PLAIN_MANNED);

        // 无人飞机
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(4, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(4, Poker.Suits.SPADE));
//        pokers.add(new Poker(4, Poker.Suits.CLUB));
//        testGetPokerInfo(pokers, PokerModel.PLAIN_UNMANNED);

        // 对牌顺子
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(4, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(4, Poker.Suits.SPADE));
//        pokers.add(new Poker(5, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(5, Poker.Suits.SPADE));
//        testGetPokerInfo(pokers, PokerModel.SHUN_ZI_PAIR);

        // 单牌顺子
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(8, Poker.Suits.CLUB));
//        pokers.add(new Poker(4, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(6, Poker.Suits.SPADE));
//        pokers.add(new Poker(5, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(7, Poker.Suits.SPADE));
//        testGetPokerInfo(pokers, PokerModel.SHUN_ZI_SINGLE);

        // 三带一对
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(4, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(4, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        testGetPokerInfo(pokers, PokerModel.THREE_ONE_PAIR);

        // 三带一单
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(4, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        testGetPokerInfo(pokers, PokerModel.THREE_ONE_SINGLE);

        // 三张牌
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        testGetPokerInfo(pokers, PokerModel.THREE);

        // 对牌
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        testGetPokerInfo(pokers, PokerModel.PAIR);

        // 单牌
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        testGetPokerInfo(pokers, PokerModel.SINGLE);

        // 炸弹
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(3, Poker.Suits.SPADE));
//        pokers.add(new Poker(3, Poker.Suits.CLUB));
//        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
//        pokers.add(new Poker(3, Poker.Suits.HEART));
//        testGetPokerInfo(pokers, PokerModel.BOMB);

        // 火箭
//        List<Poker> pokers = new ArrayList<>();
//        pokers.add(new Poker(16, null));
//        pokers.add(new Poker(17, null));
//        testGetPokerInfo(pokers, PokerModel.ROCKET);
    }

    private static void testGetPokerInfo(List<Poker> pokers, PokerModel pokerModel) {
        PokerInfo pokerInfo = getPokerInfo(pokers);
        System.out.println(pokerInfo);
        if (pokerInfo == null && pokerModel != null
                || pokerInfo.getPokerModel() != pokerModel) {
            throw new RuntimeException("不匹配！");
        }
    }

}
