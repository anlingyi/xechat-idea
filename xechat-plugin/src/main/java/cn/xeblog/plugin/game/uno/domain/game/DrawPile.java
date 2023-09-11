package cn.xeblog.plugin.game.uno.domain.game;

import cn.xeblog.plugin.game.uno.domain.card.Card;

import java.util.List;
import java.util.Stack;

public class DrawPile {
    private final Stack<Card> cards = new Stack<>();

    public DrawPile(List<Card> shuffledCards) {
        cards.addAll(shuffledCards);
    }

    public Card drawCard() {
        return cards.pop();
    }

    public int getSize() {
        return cards.size();
    }
}
