package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.HistoryMsgDTO;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.factory.MessageHandlerFactory;

/**
 * @author anlingyi
 * @date 2021/9/11 7:21 下午
 */
public class HistoryMessageHandler extends AbstractMessageHandler<HistoryMsgDTO> {

    @Override
    public void handle(Response<HistoryMsgDTO> response) {
        response.getBody().getMsgList().forEach(msg -> MessageHandlerFactory.INSTANCE.produce(msg.getType()).handle(msg));
        ConsoleAction.showSimpleMsg("------以上是历史消息------");
    }

}
