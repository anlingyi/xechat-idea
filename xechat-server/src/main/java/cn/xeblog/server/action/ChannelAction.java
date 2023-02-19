package cn.xeblog.server.action;

import cn.xeblog.commons.entity.*;
import cn.xeblog.commons.entity.game.GameRoom;
import cn.xeblog.commons.entity.game.GameRoomMsgDTO;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.cache.GameRoomCache;
import cn.xeblog.server.cache.UserCache;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.factory.ObjectFactory;
import cn.xeblog.server.service.AbstractResponseHistoryService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2020/8/14
 */
@Slf4j
public class ChannelAction {

    private static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void send(Response resp) {
        GROUP.writeAndFlush(resp);
        if (resp.getType() == MessageType.SYSTEM || resp.getType() == MessageType.USER) {
            ObjectFactory.getObject(AbstractResponseHistoryService.class).addHistory(resp);
        }
    }

    public static void send(ChannelHandlerContext ctx, Object body, MessageType messageType) {
        send(getUser(ctx), body, messageType);
    }

    public static void send(User user, Object body, MessageType messageType) {
        send(ResponseBuilder.build(user, body, messageType));
    }

    public static void add(Channel channel) {
        GROUP.add(channel);
    }

    public static String getId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asLongText();
    }

    public static User getUser(ChannelHandlerContext ctx) {
        return getUser(getId(ctx));
    }

    public static User getUser(String id) {
        return UserCache.get(id);
    }

    public static void sendOnlineUsers() {
        sendOnlineUsers(null);
    }

    public static void sendOnlineUsers(User user) {
        Response response = ResponseBuilder.build(null, new UserListMsgDTO(UserCache.listUser()), MessageType.ONLINE_USERS);
        if (user == null) {
            send(response);
        } else {
            user.send(response);
        }
    }

    public static void cleanUser(ChannelHandlerContext ctx) {
        cleanUser(getId(ctx));
    }

    public static User cleanUser(String id) {
        log.debug("清理用户, id -> {}", id);

        User user = getUser(id);
        if (user == null) {
            return null;
        }

        log.debug("清理用户, username -> {}", user.getUsername());

        GameRoom gameRoom = GameRoomCache.getGameRoomByUserId(user.getId());
        if (gameRoom != null) {
            gameRoom.getUsers().forEach((k, v) -> {
                if (v.getId().equals(user.getId())) {
                    return;
                }

                User player = UserCache.get(v.getId());
                if (player != null) {
                    player.send(ResponseBuilder.build(user, new GameRoomMsgDTO(GameRoomMsgDTO.MsgType.PLAYER_LEFT, null), MessageType.GAME_ROOM));
                }
            });
            GameRoomCache.leftRoom(gameRoom.getId(), user);
        }

        UserCache.remove(id);
        sendUserState(user, UserStateMsgDTO.State.OFFLINE);

        return user;
    }

    public static void updateUserStatus(User user) {
        send(ResponseBuilder.build(user, null, MessageType.STATUS_UPDATE));
    }

    public static void sendUserState(User user, UserStateMsgDTO.State state) {
        send(ResponseBuilder.build(null, new UserStateMsgDTO(user, state), MessageType.USER_STATE));
    }

}
