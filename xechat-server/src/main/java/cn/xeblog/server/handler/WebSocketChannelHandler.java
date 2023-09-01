package cn.xeblog.server.handler;

import cn.hutool.json.JSONUtil;
import cn.xeblog.commons.entity.Request;
import cn.xeblog.commons.enums.Protocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2023/9/1 9:05 PM
 */
@Slf4j
public class WebSocketChannelHandler extends AbstractDefaultChannelHandler<WebSocketFrame> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if (frame instanceof TextWebSocketFrame) {
            try {
                Request msg = JSONUtil.toBean(((TextWebSocketFrame) frame).text(), Request.class);
                msg.setProtocol(Protocol.WEBSOCKET);
                new RequestHandler(ctx, msg).exec();
            } catch (Exception e) {
                log.error("WebSocket消息解码失败！{}", e.getMessage());
            }
        }
    }

}
