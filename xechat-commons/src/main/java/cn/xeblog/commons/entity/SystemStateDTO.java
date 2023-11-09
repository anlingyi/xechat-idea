package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 系统状态
 *
 * @author 鼓励师
 * @date 2023/11/1 10:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemStateDTO implements Serializable {

    private String message;
    /**
     * 下面两个参数可以做成配置 如果有必要
     */
    private Boolean supportStatistics = true;
    private Boolean privateChat = true;

    public SystemStateDTO(String message) {
        this.message = message;
    }
}
