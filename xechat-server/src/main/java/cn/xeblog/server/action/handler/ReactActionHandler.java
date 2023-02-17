package cn.xeblog.server.action.handler;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.request.ReactRequest;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.server.annotation.DoAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.server.factory.ReactHandlerFactory;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author anlingyi
 * @date 2022/9/19 8:37 AM
 */
@DoAction(Action.REACT)
public class ReactActionHandler implements ActionHandler<ReactRequest> {

    @Override
    public void handle(ChannelHandlerContext ctx, ReactRequest request) {
        ReactResult result = new ReactResult();
        result.setSucceed(true);
        result.setId(request.getId());
        result.setUid(request.getUid());

        User user = UserCache.get(request.getUid());
        if (user == null) {
            result.setSucceed(false);
            result.setMsg("请先登录！");
            ctx.writeAndFlush(ResponseBuilder.react(result));
            return;
        }

        ReactHandlerFactory.INSTANCE.produce(request.getReact()).handle(user, request.getBody(), result);
    }

}
