package cn.xeblog.plugin.builder;

import cn.xeblog.commons.entity.Request;
import cn.xeblog.commons.enums.Action;

/**
 * @author anlingyi
 * @date 2020/8/20
 */
public class RequestBuilder {

    public static Request build(Object body, Action action) {
        return new Request(body, action);
    }

}
