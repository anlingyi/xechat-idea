package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;

/**
 * @author anlingyi
 * @date 2022/5/26 4:45 下午
 */
@DoMessage(MessageType.STATUS_UPDATE)
public class StatusUpdateMessageHandler extends AbstractMessageHandler<Object> {

    @Override
    protected void process(Response<Object> response) {
        DataCache.updateUser(response.getUser());
    }

}
