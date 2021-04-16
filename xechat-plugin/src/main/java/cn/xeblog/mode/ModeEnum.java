package cn.xeblog.mode;

import cn.xeblog.action.ConsoleAction;
import cn.xeblog.enums.Style;
import com.intellij.ui.JBColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

/**
 * @author anlingyi
 * @date 2020/9/1
 */
@AllArgsConstructor
@Getter
public enum ModeEnum implements Mode {
    DEFAULT("默认") {
        @Override
        public void init() {
            Style.initStyle();
        }
    },
    MUDDY_WATER("浑水摸鱼") {
        @Override
        public void handleStyle(MutableAttributeSet style) {
            StyleConstants.setForeground(style, JBColor.background());
        }

        @Override
        public void renderTextBefore() {
            ConsoleAction.render("*", Style.WARN.getByIgnoreMode());
        }
    }
    ;

    private String name;

    public static ModeEnum getMode(int index) {
        if (index < 0 || index >= values().length) {
            return null;
        }

        return values()[index];
    }

}
