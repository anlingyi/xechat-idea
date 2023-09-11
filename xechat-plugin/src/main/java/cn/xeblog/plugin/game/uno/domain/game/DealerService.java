package cn.xeblog.plugin.game.uno.domain.game;

import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.player.HandCardList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DealerService {
    public static final int TOTAL_INITIAL_HAND_CARDS = 7;

    private DealerService() {
    }

    /**
     * Cards are shuffled using the modern version of Fisher-Yates shuffle.
     * Refer https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm
     *
     * @param cards list will not be modified
     * @return a new shuffled card list
     */
    public static List<Card> shuffle(List<Card> cards) {
        var shuffledCards = new ArrayList<>(cards);
        var rand = new Random();

        for (int current = 0; current < shuffledCards.size() - 1; current++) {
            // get a random index for remaining positions, i.e. [i, CARDS_SIZE - 1)
            var randomIndex = current + rand.nextInt(cards.size() - current);

            swapCard(shuffledCards, current, randomIndex);
        }

        return shuffledCards;
    }

    public static DrawPile shuffle(DrawPile drawPile, Card lastPlayedCard) {
        var oldCards = new ArrayList<Card>();
        oldCards.add(lastPlayedCard);

        for (int i = 0; i < drawPile.getSize(); i++) {
            oldCards.add(drawPile.drawCard());
        }

        var shuffledCards = shuffle(oldCards);

        return new DrawPile(shuffledCards);
    }

    private static void swapCard(List<Card> shuffledList, int currentIndex, int randomIndex) {
        var randomCard = shuffledList.get(randomIndex);

        shuffledList.set(randomIndex, shuffledList.get(currentIndex));
        shuffledList.set(currentIndex, randomCard);
    }

    public static HandCardList[] dealInitialHandCards(DrawPile drawPile, int totalPlayers) {
        var handCardLists = new HandCardList[totalPlayers];

        for (int i = 0; i < TOTAL_INITIAL_HAND_CARDS; i++) {
            for (int p = 0; p < totalPlayers; p++) {
                if (i == 0) {
                    handCardLists[p] = new HandCardList();
                }

                handCardLists[p].addCard(drawPile.drawCard());
            }
        }

        return handCardLists;
    }
}
