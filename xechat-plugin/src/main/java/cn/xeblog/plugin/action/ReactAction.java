package cn.xeblog.plugin.action;

import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.IdUtil;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.ReactRequest;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.enums.Action;
import com.google.common.cache.*;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * @author anlingyi
 * @date 2022/9/19 6:05 AM
 */
public class ReactAction {

    private final static LoadingCache<String, Reactor> REACTOR_CACHE = CacheBuilder.newBuilder()
            .initialCapacity(16)
            .maximumSize(256)
            .expireAfterAccess(30, TimeUnit.SECONDS)
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

    public static <T> void request(Object body, React react, Consumer<T> consumer) {
        if (body == null) {
            return;
        }

        GlobalThreadPool.execute(() -> {
            String id = IdUtil.fastSimpleUUID();
            ReactRequest<Object> reactRequest = new ReactRequest<>();
            reactRequest.setId(id);
            reactRequest.setBody(body);
            reactRequest.setReact(react);

            Reactor<T> reactor = new Reactor();
            REACTOR_CACHE.put(id, reactor);

            MessageAction.send(reactRequest, Action.REACT);

            if (consumer != null) {
                T result = reactor.get();
                REACTOR_CACHE.invalidate(id);
                consumer.accept(result);
            }
        });
    }

    public static void setResult(ReactResult reactResult) {
        Reactor reactor = REACTOR_CACHE.getIfPresent(reactResult.getId());
        if (reactor != null) {
            reactor.setResult(reactResult.getResult());
        }
    }

}
