package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.Channel;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
@ToString
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    private String username;

    @Getter
    @Setter
    private UserStatus status;

    @Getter
    @Setter
    private String ip;

    @Getter
    @Setter
    private IpRegion region;

    @Getter
    @Setter
    private Role role;

    @Getter
    private transient Channel channel;

    public enum Role {
        ADMIN,
        USER
    }

    public User(String id, String username, UserStatus status, String ip, IpRegion region, Channel channel) {
        this.id = id;
        this.username = username;
        this.status = status;
        this.ip = ip;
        this.region = region;
        this.channel = channel;
    }

    public void send(Response response) {
        if (channel == null) {
            return;
        }

        channel.writeAndFlush(response);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
