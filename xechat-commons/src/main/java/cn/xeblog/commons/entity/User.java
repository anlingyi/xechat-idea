package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private UserStatus status;

    private transient Channel channel;

}
