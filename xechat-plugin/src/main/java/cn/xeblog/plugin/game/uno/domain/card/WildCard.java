package cn.xeblog.plugin.game.uno.domain.card;

import java.util.Objects;

public class WildCard extends AbstractCard {
    public WildCard(CardType type) {
        super(type, null);
    }

    public WildCard(CardType type, CardColor color) {
        super(type, color);
        CardUtil.validateColor(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WildCard wildCard = (WildCard) o;
        return getType() == wildCard.getType() && getColor() == wildCard.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getColor());
    }

    @Override
    public String toString() {
        return "WildCard{" +
            getType() + ", " + getColor() +
            '}';
    }
}
