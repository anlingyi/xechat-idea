package cn.xeblog.plugin.handler;

import cn.xeblog.commons.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author anlingyi
 * @date 2023/2/17 9:47 PM
 */
public class ReactClientHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        new ResponseHandler(msg).exec();
    }

}
