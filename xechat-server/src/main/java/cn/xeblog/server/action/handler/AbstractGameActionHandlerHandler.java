package cn.xeblog.server.action.handler;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.GameDTO;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.commons.entity.User;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
public abstract class AbstractGameActionHandlerHandler<T extends GameDTO> extends AbstractActionHandler<T> {

    @Override
    protected void process(User user, GameDTO body) {
        User opponent = null;
        if (StrUtil.isNotBlank(body.getOpponentId())) {
            opponent = ChannelAction.getUser(body.getOpponentId());
            if (opponent == null) {
                user.send(ResponseBuilder.system("对方已经下线了！"));
                return;
            }
        }

        process(user, opponent, (T) body);
    }

    protected abstract void process(User user, User opponent, T body);

}
