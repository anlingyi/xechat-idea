package cn.xeblog.commons.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author anlingyi
 * @date 2022/5/8 5:42 下午
 */
@Data
@NoArgsConstructor
public class UserMsgDTO implements Serializable {

    private Object content;

    private MsgType msgType;

    private String[] toUsers;

    public UserMsgDTO(Object content) {
        this(content, MsgType.TEXT);
    }

    public UserMsgDTO(Object content, String[] toUsers) {
        this(content, MsgType.TEXT, toUsers);
    }

    public UserMsgDTO(Object content, MsgType type) {
        this(content, type, null);
    }

    public UserMsgDTO(Object content, MsgType msgType, String[] toUsers) {
        this.content = content;
        this.msgType = msgType;
        this.toUsers = toUsers;
    }

    public enum MsgType {
        TEXT,
        IMAGE
    }

    public boolean hasUser(String username) {
        if (username != null && toUsers != null) {
            for (String toUser : toUsers) {
                if (toUser.equals(username)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setMsgType(MsgType msgType) {
        this.msgType = msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = MsgType.valueOf(msgType);
    }

}
