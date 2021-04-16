package cn.xeblog.mode;

import javax.swing.text.MutableAttributeSet;

/**
 * @author anlingyi
 * @date 2020/9/1
 */
public interface Mode {

    default void init() {}

    default void handleStyle(MutableAttributeSet style) {}

    default void renderTextBefore() {}

}
