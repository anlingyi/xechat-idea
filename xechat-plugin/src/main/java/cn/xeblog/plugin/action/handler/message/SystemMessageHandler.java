package cn.xeblog.plugin.action.handler.message;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.SystemStateDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.tools.encourage.cache.EncourageCache;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.SYSTEM)
public class SystemMessageHandler extends AbstractMessageHandler<Object> {

    @Override
    protected void process(Response<Object> response) {
        Object responseBody = response.getBody();
        if (responseBody instanceof String) {
            ConsoleAction.showSystemMsg(response.getTime(), String.valueOf(responseBody));
        } else if (responseBody instanceof SystemStateDTO) {
            SystemStateDTO systemStateDTO = (SystemStateDTO) responseBody;
            String message = systemStateDTO.getMessage();
            if (StrUtil.isNotEmpty(message)) {
                ConsoleAction.showSystemMsg(response.getTime(), message);
            }
            EncourageCache.supportStatistics = systemStateDTO.getSupportStatistics();
            EncourageCache.supportPrivateChat = systemStateDTO.getPrivateChat();
        }
    }

}
