package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 服务端响应
 *
 * @author anlingyi
 * @date 2020/5/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 发送消息的用户
     */
    private User user;
    /**
     * 内容
     */
    private T body;
    /**
     * 消息类型
     */
    private MessageType type;
    /**
     * 发送时间
     */
    private String time;

    public Response(User user, T body, MessageType type) {
        this.user = user;
        this.body = body;
        this.type = type;
        this.time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
    }

}
