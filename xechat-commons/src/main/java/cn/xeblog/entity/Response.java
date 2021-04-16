package cn.xeblog.entity;

import cn.xeblog.enums.MessageType;
import cn.xeblog.util.DateUtils;
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
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private User user;
    private T body;
    private MessageType type;
    private String time;

    public Response(User user, T body, MessageType type) {
        this.user = user;
        this.body = body;
        this.type = type;
        this.time = DateUtils.getTime();
    }

}
