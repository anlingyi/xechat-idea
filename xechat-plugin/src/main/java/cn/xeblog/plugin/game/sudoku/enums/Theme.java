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
    DARK("DARK", new Color(60, 63, 65), new Color(68, 72, 74), new Color(155, 152, 152), new Color(210, 208, 208), new Color(243, 208, 5), new Color(253, 106, 104)),
    LIGHT("LIGHT", new Color(242, 242, 242), new Color(242, 240, 219), new Color(64, 182, 224), new Color(17, 16, 16), new Color(238, 5, 207), new Color(227, 43, 61)),
    ;

    final String name;
    // 九宫格单元背景色
    final Color backgroundColorUnit1;
    final Color backgroundColorUnit2;
    // 前景色
    final Color puzzleForeground;
    final Color solutionForeground;
    // 提示颜色
    final Color tipForegroundReal;
    final Color tipForegroundResult;

    public static Theme getTheme(String name) {
        for (Theme theme : values()) {
            if (theme.getName().equals(name)) {
                return theme;
            }
        }
        return Theme.DARK;
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
