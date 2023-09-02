package cn.xeblog.commons.entity;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.Protocol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 客户端请求
 *
 * @author anlingyi
 * @date 2020/5/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 内容
     */
    private T body;

    /**
     * 客户端动作
     */
    private Action action;

    private Protocol protocol;

    public Request(T body, Action action) {
        this.body = body;
        this.action = action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setAction(String action) {
        try {
            this.action = Action.valueOf(action);
        } catch (Exception e) {
        }
    }
}
