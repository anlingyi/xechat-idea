package cn.xeblog.plugin.game.uno.domain.card;

public class CardUtil {
    private CardUtil() {
    }

    public static void validateColor(CardColor color) {
        if (color == null) {
            throw new IllegalArgumentException("Card color should be defined");
        }
    }

    public static void validateNumber(int number) {
        if (number < 0 || number > 9) {
            throw new IllegalArgumentException("Card number should between 0 and 9");
        }
    }

    public static void validateActionType(CardType type) {
        if (type == CardType.SKIP || type == CardType.REVERSE || type == CardType.DRAW_TWO) {
            return;
        }

        throw new IllegalArgumentException("Invalid action type");
    }

    public static boolean isWildCard(Card card) {
        return card.getType() == CardType.WILD_COLOR || card.getType() == CardType.WILD_DRAW_FOUR;
    }
}
