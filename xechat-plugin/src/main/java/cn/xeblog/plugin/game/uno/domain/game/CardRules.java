package cn.xeblog.plugin.game.uno.domain.game;

import cn.xeblog.plugin.game.uno.domain.card.*;

public class CardRules {
    private CardRules(){
        throw new IllegalStateException("Utility class");
    }

    public static boolean isValidNumberCard(Card topCard, NumberCard playedCard) {
        if(isSameColor(topCard, playedCard)){
            return true;
        }

        if (topCard.getType() == CardType.NUMBER) {
            return ((NumberCard) topCard).getValue() == playedCard.getValue();
        }

        return false;
    }

    public static boolean isValidActionCard(Card topCard, ActionCard playedCard) {
        if(isSameColor(topCard, playedCard)){
            return true;
        }

        return topCard.getType() == playedCard.getType();
    }

    public static boolean isValidWildCard(WildCard playedCard) {
        return playedCard.getColor() != null;
    }

    private static boolean isSameColor(Card topCard, Card playedCard) {
        return topCard.getColor() == playedCard.getColor();
    }
}
