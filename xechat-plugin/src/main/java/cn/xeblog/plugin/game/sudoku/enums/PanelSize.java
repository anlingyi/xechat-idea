package cn.xeblog.plugin.game.sudoku.enums;

import com.intellij.openapi.ui.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * 功能描述:
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/15 14:13
 */
@Getter
@AllArgsConstructor
public enum PanelSize {
    MIN("小号", 280, 350, 270, 270, 13),
    MEDIUM("中号", 410, 410, 400, 400, 18),
    MAX("大号", 510, 530, 500, 500, 23),
    ;

    String memo;
    int mainWith;
    int mainHeight;
    int tableWidth;
    int tableHeight;
    int fontSize;

    public static PanelSize getPanelSize(String name) {
        for (PanelSize panelSize : values()) {
            if (panelSize.memo.equals(name)) {
                return panelSize;
            }
        }
        return PanelSize.MIN;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (PanelSize value : PanelSize.values()) {
            comboBox.addItem(value.getMemo());
        }
        comboBox.setSelectedItem(this.getMemo());
        return comboBox;
    }
}
