package cn.xeblog.plugin.tools.browser.core;

import java.awt.*;

/**
 * @author anlingyi
 * @date 2022/8/15 2:08 PM
 */
public interface BrowserService {

    /**
     * 获取浏览器UI组件
     *
     * @return
     */
    Component getUI();

    /**
     * 加载URL
     *
     * @param url
     */
    void loadURL(String url);

    /**
     * 后退
     */
    void goBack();

    /**
     * 前进
     */
    void goForward();

    /**
     * 重新加载当前页面
     */
    void reload();

    /**
     * 浏览器关闭
     */
    void close();

    /**
     * 设置用户代理
     *
     * @param userAgent
     */
    void setUserAgent(UserAgent userAgent);

    /**
     * 添加浏览器事件监听
     *
     * @param listener
     */
    void addEventListener(BrowserEventListener listener);

}
