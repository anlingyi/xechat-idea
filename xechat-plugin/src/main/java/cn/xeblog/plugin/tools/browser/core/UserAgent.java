package cn.xeblog.plugin.tools.browser.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author anlingyi
 * @date 2022/8/15 5:15 PM
 */
@AllArgsConstructor
public enum UserAgent {

    IPHONE("iPhone") {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";
        }
    },
    ANDROID("Android") {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (Linux; Android 8.0.0; SM-G955U Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Mobile Safari/537.36";
        }
    },
    IPAD("iPad") {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (iPad; CPU OS 13_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/87.0.4280.77 Mobile/15E148 Safari/604.1";
        }
    },
    WINDOWS("Windows") {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36";
        }
    },
    MACOS("MacOS") {
        @Override
        public String getValue() {
            return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36";
        }
    },
    NATIVE("本机") {
        @Override
        public String getValue() {
            return null;
        }
    };

    @Getter
    private String name;

    public abstract String getValue();

    public static UserAgent getUserAgent(String name) {
        for (UserAgent userAgent : values()) {
            if (userAgent.getName().equals(name)) {
                return userAgent;
            }
        }
        return IPHONE;
    }

}
