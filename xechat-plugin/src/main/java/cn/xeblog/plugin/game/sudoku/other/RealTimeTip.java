package cn.xeblog.plugin.game.sudoku.other;

import com.intellij.openapi.ui.ComboBox;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

/**
 * 功能描述: 实时提示
 *
 * @author ☆程序员鼓励师☆
 * @date 2022/8/17 16:58
 */
@Getter
@AllArgsConstructor
public enum RealTimeTip {
    ENABLE(true, "是"),
    DIS_ENABLE(false, "否");

    private final boolean enabled;
    private final String memo;

    public static RealTimeTip getRealTimeTip(String name) {
        for (RealTimeTip realTimeTip : values()) {
            if (realTimeTip.memo.equals(name)) {
                return realTimeTip;
            }
        }
        return RealTimeTip.DIS_ENABLE;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (RealTimeTip value : RealTimeTip.values()) {
            comboBox.addItem(value.getMemo());
        }
        comboBox.setSelectedItem(this.getMemo());
        return comboBox;
    }
}
