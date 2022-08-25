package cn.xeblog.plugin.game.sudoku.enums;

import com.intellij.openapi.ui.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * 功能描述: 自定义面板主题
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/25 19:05
 */
@Getter
@AllArgsConstructor
public enum Theme {
    DARCULA("Darcula", new Color(60, 63, 65), new Color(68, 72, 74), new Color(155, 152, 152), new Color(210, 208, 208), new Color(243, 208, 5), new Color(253, 106, 104)),
    ;

    String name;
    // 九宫格单元背景色
    Color backgroundColorUnit1;
    Color backgroundColorUnit2;
    // 前景色
    Color puzzleForeground;
    Color solutionForeground;
    // 提示颜色
    Color tipForegroundReal;
    Color tipForegroundResult;

    public static Theme getTheme(String name) {
        for (Theme theme : values()) {
            if (theme.getName().equals(name)) {
                return theme;
            }
        }
        return Theme.DARCULA;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (Theme value : Theme.values()) {
            comboBox.addItem(value.getName());
        }
        comboBox.setSelectedItem(this.getName());
        return comboBox;
    }
}
