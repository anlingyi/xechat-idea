package cn.xeblog.plugin.game.uno.domain.player;

import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.common.Entity;

import java.util.stream.Stream;

public class Player extends Entity {
    private final String name;
    private final HandCardList handCards;

    public Player(String name, HandCardList handCards){
        super();
        this.name = name;
        this.handCards = handCards;
    }

    public String getName() {
        return name;
    }

    public Stream<Card> getHandCards() {
        return this.handCards.getCardStream();
    }

    public void addToHandCards(Card card){
        handCards.addCard(card);
    }

    public boolean removePlayedCard(Card card) {
        return handCards.removeCard(card);
    }

    public boolean hasHandCard(Card card) {
        return this.handCards.hasCard(card);
    }

    public ImmutablePlayer toImmutable() {
        return new ImmutablePlayer(this);
    }
}
