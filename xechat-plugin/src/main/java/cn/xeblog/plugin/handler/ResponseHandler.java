package cn.xeblog.plugin.handler;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.plugin.factory.MessageHandlerFactory;
import lombok.AllArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@AllArgsConstructor
public class ResponseHandler {

    private Response response;

    public void exec() {
        switch (response.getType()) {
            case HEARTBEAT:
                return;
            case USER:
                UserMsgDTO userMsgDTO = (UserMsgDTO) response.getBody();
                if (userMsgDTO.getMsgType() == UserMsgDTO.MsgType.IMAGE) {
                    break;
                }
            case SYSTEM:
            case HISTORY_MSG:
            case ONLINE_USERS:
                process();
                return;
        }

        GlobalThreadPool.execute(() -> process());
    }

    private void process() {
        MessageHandlerFactory.INSTANCE.produce(response.getType()).handle(response);
    }

}
