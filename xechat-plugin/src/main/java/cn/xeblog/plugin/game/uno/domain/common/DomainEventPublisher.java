package cn.xeblog.plugin.game.uno.domain.common;

import java.util.ArrayList;
import java.util.List;

public class DomainEventPublisher {
    private static final ThreadLocal<List<DomainEventSubscriber>> subscribers = ThreadLocal.withInitial(ArrayList::new);

    private static final ThreadLocal<Boolean> isPublishing = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private DomainEventPublisher() {
    }

    public static void subscribe(DomainEventSubscriber subscriber) {
        if (Boolean.TRUE.equals(isPublishing.get())) {
            return;
        }

        var registeredSubscribers = subscribers.get();
        registeredSubscribers.add(subscriber);
    }

    public static void unsubscribe(DomainEventSubscriber subscriber) {
        if(Boolean.TRUE.equals(isPublishing.get())){
            return;
        }

        subscribers.get().remove(subscriber);
    }

    public static void publish(final DomainEvent event) {
        if (Boolean.TRUE.equals(isPublishing.get())) {
            return;
        }

        try {
            isPublishing.set(Boolean.TRUE);

            var registeredSubscribers = subscribers.get();

            for (var subscriber : registeredSubscribers) {
                subscriber.handleEvent(event);
            }
        } finally {
            isPublishing.set(Boolean.FALSE);
        }
    }

    public static void reset() {
        subscribers.remove();
        isPublishing.remove();
    }
}
