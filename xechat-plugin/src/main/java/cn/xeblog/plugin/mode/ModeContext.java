package cn.xeblog.plugin.mode;

/**
 * @author anlingyi
 * @date 2020/9/1
 */
public class ModeContext {

    private static Mode mode = ModeEnum.DEFAULT;

    static {
        mode.init();
    }

    public static Mode getMode() {
        return mode;
    }

    public static void setMode(Mode mode) {
        if (mode == null) {
            return;
        }

        ModeContext.mode = mode;
        mode.init();
    }
}
