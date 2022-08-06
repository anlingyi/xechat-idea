package cn.xeblog.plugin.tools.read.util;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * @author LYF
 * @date 2022-07-21
 */
public class KeyFormatUtil {
    public static String format(KeyEvent e){
        String modifiersText = InputEvent.getModifiersExText(e.getModifiersEx());
        String keyStr = "";
        if (!"".equals(modifiersText)) {
            keyStr += modifiersText;
        }
        if (!keyStr.contains(KeyEvent.getKeyText(e.getKeyCode()))) {
            keyStr += keyStr.isEmpty() ? "" : "+";
            keyStr += KeyEvent.getKeyText(e.getKeyCode());
        }
        if (keyStr.contains("箭头")) {
            keyStr = keyStr.replaceAll("向上箭头", "↑")
                    .replaceAll("向下箭头", "↓")
                    .replaceAll("向右箭头", "→")
                    .replaceAll("向左箭头", "←");
        }
        return keyStr;
    }
}
