package cn.xeblog.commons.entity.react.request;

import cn.xeblog.commons.entity.react.BaseReact;
import cn.xeblog.commons.enums.Permissions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2023/2/18 8:07 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminReact extends BaseReact {

    /**
     * 操作
     */
    private Operate operate;

    /**
     * 权限
     */
    private Permissions permissions;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 配置值
     */
    private String value;

    public AdminReact(Operate operate, String value) {
        this.operate = operate;
        this.value = value;
    }

    public AdminReact(Operate operate, Permissions permissions) {
        this.operate = operate;
        this.permissions = permissions;
    }

    public AdminReact(Operate operate, Permissions permissions, String uid) {
        this.operate = operate;
        this.permissions = permissions;
        this.uid = uid;
    }

    public enum Operate {
        /**
         * 查询权限
         */
        QUERY_PERMIT,
        /**
         * 全局权限添加
         */
        GLOBAL_PERMIT_ADD,
        /**
         * 全局文件大小限制
         */
        GLOBAL_MAX_FILE_SIZE,
        /**
         * 全局权限移除
         */
        GLOBAL_PERMIT_REMOVE,
        /**
         * 用户权限添加
         */
        USER_PERMIT_ADD,
        /**
         * 用户权限移除
         */
        USER_PERMIT_REMOVE
        ;
    }

}
