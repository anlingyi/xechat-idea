package cn.xeblog.plugin.game.uno.ui.common;

import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.card.CardColor;
import cn.xeblog.plugin.game.uno.domain.card.NumberCard;

import java.awt.*;

public class StyleUtil {
    private StyleUtil() {
    }

    public static final Color      redColor     = new Color(192, 80, 77);
    public static final Color      blueColor    = new Color(31, 73, 125);
    public static final Color      greenColor   = new Color(0, 153, 0);
    public static final Color      yellowColor  = new Color(255, 204, 0);
    public static final Color      blackColor   = new Color(0, 0, 0);

    public static final String     DEFAULT_FONT = "Helvetica";

    private static final Character REVERSE_CHAR = (char) 8634;
    private static final Character SKIP_CHAR    = (char) Integer.parseInt("2718", 16);

    public static Color convertCardColor(CardColor color) {
        if (color == null) {
            return blackColor;
        }

        switch (color) {
            case RED:
                return redColor;
            case GREEN:
                return greenColor;
            case BLUE:
                return blueColor;
            case YELLOW:
                return yellowColor;
            default:
                throw new IllegalArgumentException("Unsupported card color " + color);
        }
    }

    public static String getValueToDisplay(Card card) {
        switch (card.getType()) {
            case NUMBER:
                return Integer.toString(((NumberCard) card).getValue());
            case SKIP:
                return SKIP_CHAR.toString();
            case REVERSE:
                return REVERSE_CHAR.toString();
            case DRAW_TWO:
                return "2+";
            case WILD_COLOR:
                return "W";
            case WILD_DRAW_FOUR:
                return "4+";
            default:
                throw new IllegalArgumentException("Unsupported card type " + card.getType());
        }
    }
}
