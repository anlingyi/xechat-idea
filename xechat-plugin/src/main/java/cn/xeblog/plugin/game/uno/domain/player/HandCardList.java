package cn.xeblog.plugin.game.uno.domain.player;

import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.card.CardType;
import cn.xeblog.plugin.game.uno.domain.card.CardUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class HandCardList {
    private final List<Card> handCards = new ArrayList<>();

    public void addCard(Card newCard) {
        handCards.add(newCard);
    }

    public boolean removeCard(Card card) {
        var cardToRemove = CardUtil.isWildCard(card) ? findCardOfType(card.getType()) : card;
        return handCards.remove(cardToRemove);
    }

    private Card findCardOfType(CardType type) {
        for (var card : handCards) {
            if (card.getType() == type) {
                return card;
            }
        }

        return null;
    }

    public Stream<Card> getCardStream() {
        return handCards.stream();
    }

    public boolean hasCard(Card card) {
        return CardUtil.isWildCard(card)
            ? getCardStream().anyMatch(c -> c.getType() == card.getType())
            : getCardStream().anyMatch(c -> c.equals(card));
    }

    public int size() {
        return handCards.size();
    }
}
