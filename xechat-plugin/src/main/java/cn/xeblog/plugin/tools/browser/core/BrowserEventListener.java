package cn.xeblog.plugin.tools.browser.core;

/**
 * @author anlingyi
 * @date 2022/8/15 2:24 PM
 */
public interface BrowserEventListener {

    /**
     * 浏览器地址变更
     *
     * @param url
     */
    default void onAddressChange(String url) {
    }

    /**
     * 浏览器关闭之前
     */
    default void onBeforeClose() {
    }

}
