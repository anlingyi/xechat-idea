package cn.xeblog.commons.entity;

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

    private String username;

    private UserStatus status;

    private boolean reconnected;

    private String pluginVersion;

    private String token;

}
