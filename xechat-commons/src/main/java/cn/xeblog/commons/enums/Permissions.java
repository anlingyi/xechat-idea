package cn.xeblog.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 权限
 *
 * @author anlingyi
 * @date 2023/2/18 6:51 PM
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum Permissions {

    /**
     * 发言
     */
    SPEAK(0b01),
    /**
     * 发文件
     */
    SEND_FILE(0b10),
    /**
     * 所有权限
     */
    ALL() {
        @Override
        public int getValue() {
            int value = 0;
            for (Permissions permissions : values()) {
                if (permissions == this) {
                    continue;
                }

                value |= permissions.getValue();
            }
            return value;
        }
    }
    ;

    /**
     * 权限值
     */
    private int value;

    /**
     * 是否有该权限
     *
     * @param value
     * @return
     */
    public boolean hasPermit(int value) {
        return (value & this.value) == this.value;
    }

}
