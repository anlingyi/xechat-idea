package cn.xeblog.plugin.handler;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClientHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        DataCache.ctx = ctx;
        DataCache.isOnline = true;
        ConsoleAction.clean();
        MessageAction.send(DataCache.username, Action.LOGIN);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Response msg) throws Exception {
        new ResponseHandler(msg).exec();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ConsoleAction.showSimpleMsg("不好意思，你网卡了！");
        cause.printStackTrace();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        DataCache.isOnline = false;
        DataCache.userMap = null;
        GameAction.over();
        ConsoleAction.showSimpleMsg("已断开连接！");
        ConsoleAction.setConsoleTitle("控制台");
    }

}
