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
@AllArgsConstructor
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

    private transient Channel channel;

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
