package cn.xeblog.plugin.action;

import cn.hutool.core.util.IdUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.ReactRequest;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.handler.ReactResultConsumer;
import cn.xeblog.plugin.builder.RequestBuilder;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.client.ClientConnectConsumer;
import cn.xeblog.plugin.handler.AbstractChannelInitializer;
import cn.xeblog.plugin.handler.ReactClientHandler;
import com.google.common.cache.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author anlingyi
 * @date 2022/9/19 6:05 AM
 */
public class ReactAction {

    private final static LoadingCache<String, Reactor> REACTOR_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(16)
            .maximumSize(256)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .removalListener((RemovalListener<String, Reactor>) notification -> {
                if (notification.wasEvicted()) {
                    notification.getValue().setResult(null);
                }
            })
            .build(new CacheLoader<>() {
                @Override
                public Reactor load(String key) throws Exception {
                    return null;
                }
            });

    public static <T> void request(Object body, React react, ReactResultConsumer<T> consumer) {
        request(body, react, 15, consumer);
    }

    public static <T> void request(Object body, React react, int timeout, ReactResultConsumer<T> consumer) {
        User user = DataCache.getCurrentUser();
        if (user == null) {
            consumer.failed("请先登录！");
            return;
        }

        try {
            String id = IdUtil.fastSimpleUUID();
            ReactRequest<Object> reactRequest = new ReactRequest<>();
            reactRequest.setId(id);
            reactRequest.setUid(user.getId());
            reactRequest.setBody(body);
            reactRequest.setReact(react);

            Reactor<T> reactor = new Reactor(timeout, TimeUnit.SECONDS);
            REACTOR_CACHE.put(id, reactor);

            ConnectionAction reactConnection = new ConnectionAction();
            ConnectionAction connectionAction = DataCache.connectionAction;
            if (connectionAction != null) {
                reactConnection.setHost(connectionAction.getHost());
                reactConnection.setPort(connectionAction.getPort());
            }
            reactConnection.setChannelInitializer(new AbstractChannelInitializer() {
                @Override
                public ChannelHandler addClientHandler() {
                    return new ReactClientHandler();
                }
            });

            reactConnection.exec(new ClientConnectConsumer() {
                @Override
                public void doSucceed(Channel channel) {
                    try {
                        reactor.setChannel(channel);
                        channel.closeFuture().addListener((ChannelFutureListener) future -> {
                            if (!future.channel().isActive()) {
                                reactor.setResult(null);
                            }
                        });
                        channel.writeAndFlush(RequestBuilder.build(reactRequest, Action.REACT));

                        ReactResult<T> result = reactor.get();
                        REACTOR_CACHE.invalidate(id);
                        if (result == null) {
                            consumer.failed("请求无响应！");
                            return;
                        }

                        if (result.isSucceed()) {
                            consumer.succeed(result.getData());
                        } else {
                            consumer.failed(result.getMsg());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        consumer.failed("请求失败啦！");
                    }
                }

                @Override
                public void doFailed() {
                    consumer.failed("连接服务器失败啦！");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            consumer.failed("出错啦！");
        }
    }

    public static void setResult(ReactResult reactResult) {
        Reactor reactor = REACTOR_CACHE.getIfPresent(reactResult.getId());
        if (reactor != null) {
            reactor.setResult(reactResult);
        }
    }

}
