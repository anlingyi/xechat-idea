package cn.xeblog.plugin.game.sudoku.enums;

import com.intellij.openapi.ui.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * 功能描述: 困难等级
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/12 11:35
 */
@Getter
@AllArgsConstructor
public enum Level {
    EASY(1, 30, "简单"),
    NORMAL(2, 40, "普通"),
    HARD(3, 50, "困难"),
    VERY_HARD(4, 60, "非常困难");

    final int index;
    final int blank;
    final String memo;

    public static Level getLevel(String name) {
        for (Level level : values()) {
            if (level.memo.equals(name)) {
                return level;
            }
        }
        return Level.EASY;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (Level value : Level.values()) {
            comboBox.addItem(value.getMemo());
        }
        comboBox.setSelectedItem(this.getMemo());
        return comboBox;
    }
}
