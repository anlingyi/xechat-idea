package cn.xeblog.commons.entity.game.landlords;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/3 4:09 下午
 */
@Data
public class AllocPokerDTO implements Serializable {

    /**
     * 手牌
     */
    private List<Poker> pokers;
    /**
     * 是否优先叫分
     */
    private boolean prioritized;

}
