package cn.xeblog.plugin.game.uno.domain.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * UNO Card Deck consists of 108 cards.
 * See https://en.wikipedia.org/wiki/Uno_(card_game)#/media/File:UNO_cards_deck.svg for list of card types.
 */
public class CardDeck {
    private final List<Card> cards = new ArrayList<>(108);

    public CardDeck() {
        createCards();
    }

    public List<Card> getImmutableCards() {
        return Collections.unmodifiableList(cards);
    }

    private void createCards() {
        createNumberCards();
        createActionCards();
        createWildCards();
    }

    private void createNumberCards() {
        for (var color : CardColor.values()) {
            cards.add(new NumberCard(0, color));

            for (var i = 1; i <= 9; i++) {
                cards.add(new NumberCard(i, color));
                cards.add(new NumberCard(i, color));
            }
        }
    }

    private void createActionCards() {
        for (var color : CardColor.values()) {
            for (var i = 0; i < 2; i++) {
                cards.add(new ActionCard(CardType.SKIP, color));
                cards.add(new ActionCard(CardType.REVERSE, color));
                cards.add(new ActionCard(CardType.DRAW_TWO, color));
            }
        }
    }

    private void createWildCards() {
        for (var i = 0; i < 4; i++) {
            cards.add(new WildCard(CardType.WILD_COLOR));
            cards.add(new WildCard(CardType.WILD_DRAW_FOUR));
        }
    }
}
