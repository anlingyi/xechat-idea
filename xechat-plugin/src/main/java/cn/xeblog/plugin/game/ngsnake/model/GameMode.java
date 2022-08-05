package cn.xeblog.plugin.game.ngsnake.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2022/8/5 7:34 PM
 */
@Getter
@AllArgsConstructor
public enum GameMode {
    NON_GLUTTONOUS("不贪吃模式"),
    GLUTTONOUS("贪吃模式");

    private String name;

    public static GameMode getMode(String name) {
        for (GameMode model : values()) {
            if (model.name.equals(name)) {
                return model;
            }
        }

        return GameMode.NON_GLUTTONOUS;
    }
}
