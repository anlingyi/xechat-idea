package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.ReactRequest;
import cn.xeblog.commons.entity.react.request.SendFileReact;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.entity.react.result.SendFileReactResult;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.commons.util.ThreadUtils;
import cn.xeblog.server.annotation.DoReact;
import cn.xeblog.server.builder.ResponseBuilder;

/**
 * @author anlingyi
 * @date 2022/9/19 8:53 AM
 */
@DoReact(React.SEND_FILE)
public class SendFileReactHandler extends AbstractReactHandler<ReactRequest<SendFileReact>> {

    private static final int MAX_SIZE = 1 << 20;

    @Override
    protected void process(User user, ReactRequest<SendFileReact> request) {
        SendFileReact body = request.getBody();
        ReactResult result = new ReactResult();
        SendFileReactResult reactResult = new SendFileReactResult();
        result.setResult(reactResult);
        result.setId(request.getId());
        if (body.getSize() > MAX_SIZE) {
            reactResult.setMsg("发送的文件大小不能超过" + (MAX_SIZE >> 20) + "M!");
        } else {
            reactResult.setAllowed(true);
        }

        user.send(ResponseBuilder.build(null, result, MessageType.REACT));
    }

}
