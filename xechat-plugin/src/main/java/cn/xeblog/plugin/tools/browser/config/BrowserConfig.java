package cn.xeblog.plugin.tools.browser.config;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 浏览器设置
 *
 * @author anlingyi
 * @date 2023/9/3 4:20 PM
 */
@Data
public class BrowserConfig {

    /**
     * 主页
     */
    private String homePage = "https://cn.bing.com";

    public void setHomePage(String homePage) {
        if (StrUtil.isBlank(homePage)) {
            return;
        }

        this.homePage = homePage;
    }
}
