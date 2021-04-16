package cn.xeblog.enums;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public enum UserStatus implements Status {
    WORKING {
        @Override
        public String alias() {
            return "工作中";
        }
    },
    FISHING {
        @Override
        public String alias() {
            return "摸鱼中";
        }
    },
    PLAYING {
        @Override
        public String alias() {
            return "游戏中";
        }
    }
    ;
}
