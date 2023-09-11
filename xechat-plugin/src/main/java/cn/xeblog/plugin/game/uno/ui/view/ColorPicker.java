package cn.xeblog.plugin.game.uno.ui.view;

import cn.xeblog.plugin.game.uno.domain.card.CardColor;

import javax.swing.*;
import java.util.ArrayList;

public class ColorPicker {
    private final ArrayList<String> colors;

    private ColorPicker() {
        colors = new ArrayList<>();

        for (var color : CardColor.values()) {
            colors.add(color.name());
        }
    }

    // See Bill Pugh Singleton approach
    // Ref: https://www.journaldev.com/1377/java-singleton-design-pattern-best-practices-examples#bill-pugh-singleton
    private static class SingletonHelper {
        private static final ColorPicker INSTANCE = new ColorPicker();
    }

    public static ColorPicker getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public CardColor show() {
        String pickedColor = (String) JOptionPane.showInputDialog(
            null,
            "Choose a color",
            "Wild Card",
            JOptionPane.PLAIN_MESSAGE,
            null,
            colors.toArray(),
            null
        );

        return CardColor.valueOf(pickedColor);
    }
}
