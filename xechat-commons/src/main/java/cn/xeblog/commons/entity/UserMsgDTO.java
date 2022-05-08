package cn.xeblog.commons.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2022/5/8 5:42 下午
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMsgDTO implements Serializable {

    private Object content;

    private MsgType msgType;

    public UserMsgDTO(Object content) {
        this.content = content;
        this.msgType = MsgType.TEXT;
    }

    public enum MsgType {
        TEXT,
        IMAGE
    }

}
