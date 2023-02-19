package cn.xeblog.server.service;

import cn.hutool.core.bean.BeanUtil;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;

import java.util.List;

/**
 * @author anlingyi
 * @date 2021/9/11 4:58 下午
 */
public abstract class AbstractResponseHistoryService {

    public final void addHistory(Response response) {
        addHistoryHandler(clone(response));
    }

    protected abstract void addHistoryHandler(Response response);

    public abstract List<Response> getHistory(int limit);

    protected Response clone(Response origin) {
        Response resp = new Response();
        resp.setBody(origin.getBody());
        resp.setTime(origin.getTime());
        resp.setType(origin.getType());
        User user = origin.getUser();
        if (user != null) {
            User newUser = BeanUtil.copyProperties(user, User.class, "channel");
            resp.setUser(newUser);
        }
        return resp;
    }

}
