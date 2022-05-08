package cn.xeblog.plugin.enums;

import cn.xeblog.plugin.mode.ModeContext;
import com.intellij.ui.JBColor;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2020/8/21
 */
public enum Style {
    DEFAULT,
    USER_NAME {
        @Override
        protected void init() {
            super.init();
            StyleConstants.setBold(style, true);
        }
    },
    SYSTEM_MSG {
        @Override
        protected void init() {
            super.init();
            StyleConstants.setForeground(style, new Color(232, 81, 88));
            StyleConstants.setBold(style, true);
        }
    },
    WARN {
        @Override
        protected void init() {
            super.init();
            StyleConstants.setForeground(style, new Color(232, 81, 88));
        }
    }
    ;

    protected MutableAttributeSet style;

    protected void init() {
        style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, JBColor.foreground());
    }

    public MutableAttributeSet get() {
        ModeContext.getMode().handleStyle(style);
        return style;
    }

    public MutableAttributeSet getByIgnoreMode() {
        return style;
    }

    public static void initStyle() {
        for (Style style : values()) {
            style.init();
        }
    }

}
