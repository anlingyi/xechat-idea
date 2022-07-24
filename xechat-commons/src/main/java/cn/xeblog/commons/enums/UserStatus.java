package cn.xeblog.commons.enums;

import lombok.Getter;

/**
 * 用户状态
 *
 * @author anlingyi
 * @date 2020/6/1
 */
public enum UserStatus implements Status {

    WORKING("工") {
        @Override
        public String alias() {
            return "工作中";
        }
    },
    FISHING("鱼") {
        @Override
        public String alias() {
            return "摸鱼中";
        }
    },
    PLAYING("戏") {
        @Override
        public String alias() {
            return "游戏中";
        }
    };

    /**
     * 简称
     */
    @Getter
    private String name;

    private UserStatus(String name) {
        this.name = name;
    }

}
