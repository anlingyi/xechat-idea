package cn.xeblog.plugin.action.handler.message;

import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ReactAction;
import cn.xeblog.plugin.annotation.DoMessage;

/**
 * @author anlingyi
 * @date 2022/9/19 8:20 AM
 */
@DoMessage(MessageType.REACT)
public class ReactMessageHandler extends AbstractMessageHandler<ReactResult> {

    @Override
    protected void process(Response<ReactResult> response) {
        ReactAction.setResult(response.getBody());
    }

}
