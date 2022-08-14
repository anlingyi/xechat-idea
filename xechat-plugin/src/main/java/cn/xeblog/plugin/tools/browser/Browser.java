package cn.xeblog.plugin.tools.browser;

import cn.xeblog.plugin.annotation.DoTool;
import cn.xeblog.plugin.tools.AbstractTool;
import cn.xeblog.plugin.tools.Tools;
import cn.xeblog.plugin.tools.browser.ui.BrowserUI;

import java.awt.*;

/**
 * @author anlingyi
 * @date 2022/8/14 11:12 AM
 */
@DoTool(Tools.BROWSER)
public class Browser extends AbstractTool {

    @Override
    protected void init() {
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(new BrowserUI());
    }

}
