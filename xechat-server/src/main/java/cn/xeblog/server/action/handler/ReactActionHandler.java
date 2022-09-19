package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.request.ReactRequest;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.factory.ReactHandlerFactory;

/**
 * @author anlingyi
 * @date 2022/9/19 8:37 AM
 */
@DoAction(Action.REACT)
public class ReactActionHandler extends AbstractActionHandler<ReactRequest> {

    @Override
    protected void process(User user, ReactRequest body) {
        ReactHandlerFactory.INSTANCE.produce(body.getReact()).handle(user, body);
    }

}
