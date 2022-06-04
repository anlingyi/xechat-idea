package cn.xeblog.commons.entity.game.landlords;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/3 4:09 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllocPokerDTO implements Serializable {

    /**
     * 手牌
     */
    private List<Poker> pokers;
    /**
     * 底牌
     */
    private List<Poker> lastPokers;
    /**
     * 是否优先叫分
     */
    private boolean prioritized;

}
