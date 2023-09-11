package cn.xeblog.plugin.game.uno.domain.card;

import java.io.Serializable;

public interface Card extends Serializable {
    CardType getType();
    CardColor getColor();
}
