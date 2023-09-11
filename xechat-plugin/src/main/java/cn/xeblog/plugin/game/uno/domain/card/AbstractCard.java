package cn.xeblog.plugin.game.uno.domain.card;

public abstract class AbstractCard implements Card {
    private final CardType type;
    private final CardColor color;

    protected AbstractCard(CardType type, CardColor color) {
        this.type = type;
        this.color = color;
    }

    @Override
    public CardType getType() {
        return type;
    }

    @Override
    public CardColor getColor() {
        return color;
    }

    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
