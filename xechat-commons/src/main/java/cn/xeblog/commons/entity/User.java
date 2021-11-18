package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.UserStatus;
import io.netty.channel.Channel;
import lombok.*;

import java.io.Serializable;

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
    private String username;

    @Getter
    @Setter
    private UserStatus status;

    private transient Channel channel;

    public void send(Response response) {
        if (channel == null) {
            return;
        }

        channel.writeAndFlush(response);
    }

}
