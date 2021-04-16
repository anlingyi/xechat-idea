package cn.xeblog.builder;

import cn.xeblog.entity.Request;
import cn.xeblog.enums.Action;

/**
 * @author anlingyi
 * @date 2020/8/20
 */
public class RequestBuilder {

    public static Request build(Object body, Action action) {
        return new Request(body, action);
    }

}
