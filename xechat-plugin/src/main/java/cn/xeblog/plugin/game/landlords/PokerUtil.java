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
     * 不洗牌
     *
     * @param pokers
     */
    public static void notShuffle(List<Poker> pokers) {
        int size = pokers.size();
        Random random = new Random();
        Collections.rotate(pokers, random.nextInt(size));
        Collections.rotate(pokers, random.nextInt(size));
        Collections.rotate(pokers, random.nextInt(size));
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
                    return before;
                }
                return after;
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
        return allocPokers(true);
    }

    /**
     * 分配扑克牌
     *
     * @param shuffled 是否洗牌
     * @return
     */
    public static List<List<Poker>> allocPokers(boolean shuffled) {
        int handTotal = 17;
        List<List<Poker>> list = new ArrayList<>(4);
        List<Poker> deck = genPokers();
        if (shuffled) {
            shuffle(deck);
        } else {
            notShuffle(deck);
        }

        List<Poker> player1 = new ArrayList<>(handTotal);
        List<Poker> player2 = new ArrayList<>(handTotal);
        List<Poker> player3 = new ArrayList<>(handTotal);
        list.add(player1);
        list.add(player2);
        list.add(player3);

        int index = 0;
        Integer[] allocBases = {2, 4, 4, 6, 1};
        for (Integer base : allocBases) {
            for (int j = 0; j < list.size(); j++) {
                list.get(j).addAll(deck.subList(index, index + base));
                index += base;
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
     * @param list
     * @return
     */
    public static PokerInfo getPokerInfo(List<Poker> list) {
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        List<Poker> pokers = new ArrayList<>(list);
        int size = pokers.size();
        PokerInfo pokerInfo = new PokerInfo();

        if (size > 1) {
            if (size == 2) {
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
            boolean resetShunzi = false;
            boolean resetShunziMark = false;
            Set<Integer> keys = pokersMap.keySet();
            List<Poker> maxPokers = null;
            List<Poker> singlePokers = new ArrayList<>();
            List<List<Poker>> pairPokers = new ArrayList<>();

            for (Integer key : keys) {
                List<Poker> pokerList = pokersMap.get(key);
                int pokerSize = pokerList.size();
                int maxPokerSize = maxPokers == null ? 0 : maxPokers.size();

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

            List<Poker> plainList = new ArrayList<>();
            List<Integer> sortedValues = keys.stream().sorted().collect(Collectors.toList());
            int len = sortedValues.size();
            for (int i = 0; i < len; i++) {
                int currentValue = sortedValues.get(i);
                List<Poker> currentPokerList = pokersMap.get(currentValue);
                int currentSize = currentPokerList.size();
                if (currentSize == maxLen) {
                    if ((maxLen != minLen || !resetShunzi) && currentValue > value) {
                        value = currentValue;
                    }
                }

                if (shunzi > maxShunzi) {
                    maxShunzi = shunzi;
                }

                if (i == len - 1) {
                    break;
                }

                int nextValue = sortedValues.get(i + 1);
                List<Poker> nextPokerList = pokersMap.get(nextValue);
                if (currentValue < 15 && nextValue < 15 && currentValue + 1 == nextValue && nextPokerList.size() == currentSize) {
                    if (currentSize == 3) {
                        if (shunzi == 1) {
                            plainList.addAll(currentPokerList);
                        }
                        plainList.addAll(nextPokerList);
                    }
                    shunzi++;
                    resetShunzi = false;
                } else {
                    resetShunzi = true;
                    resetShunziMark = true;
                    shunzi = 1;
                    if (maxLen < 3) {
                        maxShunzi = 0;
                    }
                }
            }

            pokerInfo.setPokers(pokers);
            pokerInfo.setValue(value);
            if (size == 2 && maxLen == 2) {
                // 对牌
                pokerInfo.setPokerModel(PokerModel.PAIR);
            } else if (size == 3 && maxLen == 3) {
                // 三张牌
                pokerInfo.setPokerModel(PokerModel.THREE);
            } else if (maxLen == 4) {
                maxPokers.forEach(poker -> poker.setSort(1));
                if (size == 4) {
                    // 炸弹
                    pokerInfo.setPokerModel(PokerModel.BOMB);
                } else if (size == 6) {
                    // 四带二单
                    pokerInfo.setPokerModel(PokerModel.FOUR_TWO_SINGLE);
                } else if (size == 8 && minLen == 2) {
                    // 四带两对
                    pokerInfo.setPokerModel(PokerModel.FOUR_TWO_PAIR);
                } else {
                    return null;
                }
            } else if (size == 4 && maxLen == 3) {
                // 三带一单
                pokerInfo.setPokerModel(PokerModel.THREE_ONE_SINGLE);
                maxPokers.forEach(poker -> poker.setSort(1));
            } else if (size == 5 && maxLen == 3 && minLen == 2) {
                // 三带一对
                pokerInfo.setPokerModel(PokerModel.THREE_ONE_PAIR);
                maxPokers.forEach(poker -> poker.setSort(1));
            } else if (maxShunzi > 1) {
                if (size > 7 && maxShunzi > 1 && maxLen == 3 && (minLen == 2 && pairPokers.size() == maxShunzi || maxShunzi * 4 == size)) {
                    // 载人飞机
                    pokerInfo.setPokerModel(PokerModel.PLAIN_MANNED);
                    plainList.forEach(poker -> poker.setSort(1));
                } else if (maxLen - minLen == 0) {
                    if (!resetShunziMark && maxShunzi > 4 && maxLen == 1) {
                        // 单牌顺子
                        pokerInfo.setPokerModel(PokerModel.SHUN_ZI_SINGLE);
                    } else if (!resetShunziMark && maxShunzi > 2 && maxLen == 2) {
                        // 对牌顺子
                        pokerInfo.setPokerModel(PokerModel.SHUN_ZI_PAIR);
                    } else if (maxShunzi > 1 && maxLen == 3 && maxShunzi * 3 == size) {
                        // 无人飞机
                        pokerInfo.setPokerModel(PokerModel.PLAIN_UNMANNED);
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }

            sorted(pokers, false);
        } else {
            // 单牌
            pokerInfo.setPokers(pokers);
            pokerInfo.setPokerModel(PokerModel.SINGLE);
            pokerInfo.setValue(pokers.get(0).getValue());
        }

        return pokerInfo;
    }

    public static void main(String[] args) {
//        testPokerInfo();
        List<List<Poker>> pokerList = allocPokers(false);
        System.out.println("玩家1-手牌 -> " + pokerList.get(0));
        System.out.println("玩家2-手牌 -> " + pokerList.get(1));
        System.out.println("玩家3-手牌 -> " + pokerList.get(2));
        System.out.println("底牌 -> " + pokerList.get(3));
    }

    private static void testGetPokerInfo(List<Poker> pokers, PokerModel pokerModel) {
        PokerInfo pokerInfo = getPokerInfo(pokers);
        System.out.println(pokerInfo);
        if ((pokerInfo == null && pokerModel != null)
                || (pokerInfo != null && pokerInfo.getPokerModel() != pokerModel)) {
            throw new RuntimeException("不匹配！");
        }
    }

    private static void testPokerInfo() {
        // 四带两对
        List<Poker> pokers = new ArrayList<>();
        pokers.add(new Poker(15, Poker.Suits.HEART));
        pokers.add(new Poker(3, Poker.Suits.SPADE));
        pokers.add(new Poker(3, Poker.Suits.CLUB));
        pokers.add(new Poker(14, Poker.Suits.CLUB));
        pokers.add(new Poker(14, Poker.Suits.SPADE));
        pokers.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers.add(new Poker(3, Poker.Suits.HEART));
        pokers.add(new Poker(15, Poker.Suits.SPADE));
        testGetPokerInfo(pokers, PokerModel.FOUR_TWO_PAIR);

        // 四带两单
        List<Poker> pokers2 = new ArrayList<>();
        pokers2.add(new Poker(5, Poker.Suits.SPADE));
        pokers2.add(new Poker(5, Poker.Suits.CLUB));
        pokers2.add(new Poker(4, Poker.Suits.CLUB));
        pokers2.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers2.add(new Poker(3, Poker.Suits.HEART));
        pokers2.add(new Poker(5, Poker.Suits.HEART));
        testGetPokerInfo(pokers2, PokerModel.FOUR_TWO_SINGLE);

        // 载人飞机
        List<Poker> pokers3 = new ArrayList<>();
        pokers3.add(new Poker(3, Poker.Suits.SPADE));
        pokers3.add(new Poker(3, Poker.Suits.CLUB));
        pokers3.add(new Poker(15, Poker.Suits.CLUB));
        pokers3.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers3.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers3.add(new Poker(15, Poker.Suits.SPADE));
        pokers3.add(new Poker(14, Poker.Suits.SPADE));
        pokers3.add(new Poker(14, Poker.Suits.SPADE));
        pokers3.add(new Poker(4, Poker.Suits.SPADE));
        pokers3.add(new Poker(4, Poker.Suits.CLUB));
        testGetPokerInfo(pokers3, PokerModel.PLAIN_MANNED);

        // 载人飞机
        List<Poker> pokers4 = new ArrayList<>();
        pokers4.add(new Poker(3, Poker.Suits.SPADE));
        pokers4.add(new Poker(3, Poker.Suits.CLUB));
        pokers4.add(new Poker(5, Poker.Suits.SPADE));
        pokers4.add(new Poker(5, Poker.Suits.CLUB));
        pokers4.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers4.add(new Poker(6, Poker.Suits.CLUB));
        pokers4.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers4.add(new Poker(7, Poker.Suits.DIAMOND));
        pokers4.add(new Poker(6, Poker.Suits.SPADE));
        pokers4.add(new Poker(6, Poker.Suits.HEART));
        pokers4.add(new Poker(7, Poker.Suits.SPADE));
        pokers4.add(new Poker(7, Poker.Suits.CLUB));
        testGetPokerInfo(pokers4, PokerModel.PLAIN_MANNED);

        // 载人飞机
        List<Poker> pokers5 = new ArrayList<>();
        pokers5.add(new Poker(3, Poker.Suits.SPADE));
        pokers5.add(new Poker(3, Poker.Suits.CLUB));
        pokers5.add(new Poker(5, Poker.Suits.SPADE));
        pokers5.add(new Poker(5, Poker.Suits.CLUB));
        pokers5.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers5.add(new Poker(7, Poker.Suits.CLUB));
        pokers5.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers5.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers5.add(new Poker(7, Poker.Suits.SPADE));
        pokers5.add(new Poker(7, Poker.Suits.HEART));
        pokers5.add(new Poker(4, Poker.Suits.SPADE));
        pokers5.add(new Poker(4, Poker.Suits.CLUB));
        testGetPokerInfo(pokers5, PokerModel.PLAIN_MANNED);

        // 载人飞机
        List<Poker> pokers6 = new ArrayList<>();
        pokers6.add(new Poker(5, Poker.Suits.SPADE));
        pokers6.add(new Poker(5, Poker.Suits.CLUB));
        pokers6.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers6.add(new Poker(6, Poker.Suits.CLUB));
        pokers6.add(new Poker(6, Poker.Suits.SPADE));
        pokers6.add(new Poker(6, Poker.Suits.HEART));
        pokers6.add(new Poker(3, Poker.Suits.SPADE));
        pokers6.add(new Poker(4, Poker.Suits.CLUB));
        testGetPokerInfo(pokers6, PokerModel.PLAIN_MANNED);

        // 无人飞机
        List<Poker> pokers7 = new ArrayList<>();
        pokers7.add(new Poker(3, Poker.Suits.SPADE));
        pokers7.add(new Poker(3, Poker.Suits.CLUB));
        pokers7.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers7.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers7.add(new Poker(4, Poker.Suits.SPADE));
        pokers7.add(new Poker(4, Poker.Suits.CLUB));
        testGetPokerInfo(pokers7, PokerModel.PLAIN_UNMANNED);

        // 对牌顺子
        List<Poker> pokers8 = new ArrayList<>();
        pokers8.add(new Poker(3, Poker.Suits.SPADE));
        pokers8.add(new Poker(3, Poker.Suits.CLUB));
        pokers8.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers8.add(new Poker(4, Poker.Suits.SPADE));
        pokers8.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers8.add(new Poker(5, Poker.Suits.SPADE));
        testGetPokerInfo(pokers8, PokerModel.SHUN_ZI_PAIR);

        // 单牌顺子
        List<Poker> pokers9 = new ArrayList<>();
        pokers9.add(new Poker(3, Poker.Suits.SPADE));
        pokers9.add(new Poker(8, Poker.Suits.CLUB));
        pokers9.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers9.add(new Poker(6, Poker.Suits.SPADE));
        pokers9.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers9.add(new Poker(7, Poker.Suits.SPADE));
        pokers9.add(new Poker(9, Poker.Suits.SPADE));
        pokers9.add(new Poker(10, Poker.Suits.SPADE));
        testGetPokerInfo(pokers9, PokerModel.SHUN_ZI_SINGLE);

        // 三带一对
        List<Poker> pokers10 = new ArrayList<>();
        pokers10.add(new Poker(3, Poker.Suits.SPADE));
        pokers10.add(new Poker(3, Poker.Suits.CLUB));
        pokers10.add(new Poker(4, Poker.Suits.DIAMOND));
        pokers10.add(new Poker(4, Poker.Suits.SPADE));
        pokers10.add(new Poker(3, Poker.Suits.DIAMOND));
        testGetPokerInfo(pokers10, PokerModel.THREE_ONE_PAIR);

        // 三带一单
        List<Poker> pokers11 = new ArrayList<>();
        pokers11.add(new Poker(3, Poker.Suits.SPADE));
        pokers11.add(new Poker(3, Poker.Suits.CLUB));
        pokers11.add(new Poker(4, Poker.Suits.SPADE));
        pokers11.add(new Poker(3, Poker.Suits.DIAMOND));
        testGetPokerInfo(pokers11, PokerModel.THREE_ONE_SINGLE);

        // 三张牌
        List<Poker> pokers12 = new ArrayList<>();
        pokers12.add(new Poker(3, Poker.Suits.SPADE));
        pokers12.add(new Poker(3, Poker.Suits.CLUB));
        pokers12.add(new Poker(3, Poker.Suits.DIAMOND));
        testGetPokerInfo(pokers12, PokerModel.THREE);

        // 对牌
        List<Poker> pokers13 = new ArrayList<>();
        pokers13.add(new Poker(3, Poker.Suits.SPADE));
        pokers13.add(new Poker(3, Poker.Suits.DIAMOND));
        testGetPokerInfo(pokers13, PokerModel.PAIR);

        // 单牌
        List<Poker> pokers14 = new ArrayList<>();
        pokers14.add(new Poker(3, Poker.Suits.SPADE));
        testGetPokerInfo(pokers14, PokerModel.SINGLE);

        // 炸弹
        List<Poker> pokers15 = new ArrayList<>();
        pokers15.add(new Poker(3, Poker.Suits.SPADE));
        pokers15.add(new Poker(3, Poker.Suits.CLUB));
        pokers15.add(new Poker(3, Poker.Suits.DIAMOND));
        pokers15.add(new Poker(3, Poker.Suits.HEART));
        testGetPokerInfo(pokers15, PokerModel.BOMB);

        // 火箭
        List<Poker> pokers16 = new ArrayList<>();
        pokers16.add(new Poker(16, null));
        pokers16.add(new Poker(17, null));
        testGetPokerInfo(pokers16, PokerModel.ROCKET);

        // 单牌顺子不匹配
        List<Poker> pokers17 = new ArrayList<>();
        pokers17.add(new Poker(3, Poker.Suits.SPADE));
        pokers17.add(new Poker(8, Poker.Suits.CLUB));
        pokers17.add(new Poker(6, Poker.Suits.SPADE));
        pokers17.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers17.add(new Poker(7, Poker.Suits.SPADE));
        pokers17.add(new Poker(9, Poker.Suits.SPADE));
        pokers17.add(new Poker(10, Poker.Suits.SPADE));
        testGetPokerInfo(pokers17, null);

        // 对牌顺子不匹配
        List<Poker> pokers18 = new ArrayList<>();
        pokers18.add(new Poker(3, Poker.Suits.SPADE));
        pokers18.add(new Poker(3, Poker.Suits.CLUB));
        pokers18.add(new Poker(5, Poker.Suits.DIAMOND));
        pokers18.add(new Poker(5, Poker.Suits.SPADE));
        pokers18.add(new Poker(6, Poker.Suits.SPADE));
        pokers18.add(new Poker(6, Poker.Suits.SPADE));
        pokers18.add(new Poker(7, Poker.Suits.SPADE));
        pokers18.add(new Poker(7, Poker.Suits.SPADE));
        testGetPokerInfo(pokers18, null);
    }

}
