package cn.xeblog.plugin.game.uno.domain.card;

import java.util.Objects;

public class NumberCard extends AbstractCard {
    private final int value;

    public NumberCard(int value, CardColor color) {
        super(CardType.NUMBER, color);

        CardUtil.validateColor(color);

        CardUtil.validateNumber(value);
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumberCard that = (NumberCard) o;
        return value == that.value && getColor() == that.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, getColor());
    }

    @Override
    public String toString() {
        return "NumberCard{" +
            value + ", " + getColor() +
            '}';
    }
}
