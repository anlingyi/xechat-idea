package cn.xeblog.commons.entity.game.landlords;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2022/6/2 1:47 下午
 */
@Data
@NoArgsConstructor
public class Poker implements Serializable {

    /**
     * 牌值，3~10, 11 -> J, 12 -> Q, 13 -> K, 14 -> A, 15 -> 2, 16 -> 小王, 17 -> 大王
     */
    private int value;

    /**
     * 花色
     */
    private Suits suits;

    /**
     * 排序
     */
    private transient int sort;

    public Poker(int value, Suits suits) {
        this.value = value;
        this.suits = suits;
    }

    public enum Suits {
        /**
         * 黑桃
         */
        SPADE,
        /**
         * 红桃
         */
        HEART,
        /**
         * 方块
         */
        DIAMOND,
        /**
         * 梅花
         */
        CLUB
    }

}
