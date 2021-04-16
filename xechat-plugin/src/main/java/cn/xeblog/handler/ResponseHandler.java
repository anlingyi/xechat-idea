package cn.xeblog.handler;

import cn.xeblog.entity.Response;
import lombok.AllArgsConstructor;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@AllArgsConstructor
public class ResponseHandler {

    private Response response;

    public void exec() {
        response.getType().produce().handle(response);
    }
}
