package cn.xeblog.plugin.handler;

import cn.xeblog.commons.entity.LoginDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.UserStatus;
import cn.xeblog.plugin.action.ConnectionAction;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.GameAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.commons.entity.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author anlingyi
 * @date 2020/5/29
 */
public class XEChatClientHandler extends SimpleChannelInboundHandler<Response> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        DataCache.ctx = ctx;
        DataCache.isOnline = true;

        boolean reconnected = DataCache.reconnected;
        if (!reconnected) {
            ConsoleAction.clean();
            ConsoleAction.showSimpleMsg("正在登录中...");
        }

        UserStatus status = DataCache.userStatus;
        if (status == null) {
            status = UserStatus.FISHING;
        }
        if (GameAction.playing()) {
            status = UserStatus.PLAYING;
        }
        MessageAction.send(new LoginDTO(DataCache.username, status, reconnected), Action.LOGIN);
        DataCache.reconnected = false;
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

        if (DataCache.reconnected) {
            ConnectionAction connectionAction = DataCache.connectionAction;
            if (connectionAction == null) {
                connectionAction = new ConnectionAction();
            }

            ConsoleAction.showSimpleMsg("正在重新连接服务器...");
            connectionAction.exec(null);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case WRITER_IDLE:
                    MessageAction.send(null, Action.HEARTBEAT);
                    break;
                case READER_IDLE:
                    break;
                case ALL_IDLE:
                    ctx.close();
                    DataCache.reconnected = true;
            }
        }
    }
}
