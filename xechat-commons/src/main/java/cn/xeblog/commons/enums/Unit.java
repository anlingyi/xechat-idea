package cn.xeblog.commons.enums;

import lombok.Getter;

/**
 * 单位
 *
 * @author nn200433
 * @date 2022-04-19 11:54:56
 */
public enum Unit {

    /**
     * 公制单位
     */
    M("m"),

    /**
     * 英制单位
     */
    I("i");

    @Getter
    private String value;

    private Unit(String value) {
        this.value = value;
    }

}