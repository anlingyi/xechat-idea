package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.Platform;
import cn.xeblog.commons.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2022/4/3 4:29 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO implements Serializable {

    /**
     * 昵称
     */
    private String username;

    /**
     * 状态
     */
    private UserStatus status;

    /**
     * 是否是重连
     */
    private boolean reconnected;

    /**
     * 插件版本
     */
    private String pluginVersion;

    /**
     * 令牌
     */
    private String token;

    /**
     * 全局唯一ID
     */
    private String uuid;

    /**
     * 来源平台
     */
    private Platform platform;

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        try {
            this.status = UserStatus.valueOf(status);
        } catch (Exception e) {
            this.status = UserStatus.FISHING;
        }
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
    }

    public void setPlatform(String platform) {
        try {
            this.platform = Platform.valueOf(platform);
        } catch (Exception e) {
            this.platform = Platform.IDEA;
        }
    }

}
