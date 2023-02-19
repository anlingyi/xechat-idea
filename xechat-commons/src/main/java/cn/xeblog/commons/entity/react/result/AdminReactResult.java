package cn.xeblog.commons.entity.react.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2023/2/18 8:15 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminReactResult {

    /**
     * 全局权限值
     */
    private int globalPermit;

    /**
     * 文件大小限制
     */
    private int maxFileSize;

}
