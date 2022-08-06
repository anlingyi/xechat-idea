package cn.xeblog.plugin.factory;

import cn.xeblog.plugin.annotation.DoTool;
import cn.xeblog.plugin.tools.AbstractTool;
import cn.xeblog.plugin.tools.Tools;

/**
 * @author anlingyi
 * @date 2022/8/5 5:31 上午
 */
public class ToolsFactory {

    private static ObjectFactory objectFactory = new ObjectFactory(DoTool.class);

    public static AbstractTool produce(Tools tools) {
        Object object = objectFactory.produce(tools);
        if (object == null) {
            return null;
        }

        return (AbstractTool) object;
    }

}
