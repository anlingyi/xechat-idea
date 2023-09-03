package cn.xeblog.server.handler;

import cn.xeblog.server.util.FileUtil;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author anlingyi
 * @date 2023/9/1 9:04 PM
 */
public class HttpChannelHandler extends AbstractDefaultChannelHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");

        String uri = fullHttpRequest.uri();
        if (uri.startsWith("/download/")) {
            String fileName = uri.replace("/download/", "");
            if (!fileName.startsWith(".")) {
                byte[] bytes = FileUtil.getFile(fileName);
                if (bytes != null) {
                    response.content().writeBytes(bytes);
                    response.headers().set(HttpHeaderNames.CACHE_CONTROL, "max-age=86400");
                    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "image/jpeg,image/png,image/gif,image/bmp");
                    response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, "inline;filename=\"" + fileName + "\"");
                    response.setStatus(HttpResponseStatus.OK);
                }
            }
        } else {
            response.setStatus(HttpResponseStatus.OK);
            response.content().writeBytes("Hello World!".getBytes(CharsetUtil.UTF_8));
        }

        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

}
