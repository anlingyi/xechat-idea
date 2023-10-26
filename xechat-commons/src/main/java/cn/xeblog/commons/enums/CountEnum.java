package cn.xeblog.commons.enums;

import lombok.Getter;

/**
 * @author 鼓励师
 * @date 2023/8/30 11:01
 */
@Getter
public enum CountEnum {

    COUNTRY("country", "国家"),
    PROVINCE("province", "省份"),
    CITY("city", "城市"),
    ISP("isp", "运营商"),
    USER_STATUS("status", "用户状态"),
    ;

    CountEnum(String countType, String desc) {
        this.countType = countType;
        this.desc = desc;
    }

    private final String countType;
    private final String desc;

    public static CountEnum getByIndex(int index) {
        for (int i = 0; i < values().length; i++) {
            if (index == i) {
                return values()[i];
            }
        }
        return null;
    }

}

