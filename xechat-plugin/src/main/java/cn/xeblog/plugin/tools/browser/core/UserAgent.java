package cn.xeblog.plugin.tools.browser.core;

/**
 * @author anlingyi
 * @date 2022/8/15 5:15 PM
 */
public enum UserAgent {

    IPHONE {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
        }
    },
    IPAD {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (iPad; CPU OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/87.0.4280.77 Mobile/15E148 Safari/604.1";
        }
    };

    public abstract String getValue();

}
