package cn.xeblog.plugin.tools;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2022/8/5 5:15 上午
 */
@Getter
@AllArgsConstructor
public enum Tools {
    READ("阅读", false),
    BROWSER("浏览器", false);

    /**
     * 工具名称
     */
    private String name;

    /**
     * 是否必须要登录
     */
    private boolean requiredLogin;

    public static Tools getTool(int index) {
        Tools[] tools = values();
        if (index < 0 || index >= tools.length) {
            return null;
        }

        return tools[index];
    }

}
