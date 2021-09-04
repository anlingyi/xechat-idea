package cn.xeblog.plugin.util;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;

/**
 * @author anlingyi
 * @date 2021/9/4 9:26 上午
 */
public class IdeaUtils {

    private final static String ID = "cn.xeblog.xechat.plugin";

    public static String getPluginVersion() {
        IdeaPluginDescriptor pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(ID));
        return pluginDescriptor == null ? "???" : pluginDescriptor.getVersion();
    }

}
