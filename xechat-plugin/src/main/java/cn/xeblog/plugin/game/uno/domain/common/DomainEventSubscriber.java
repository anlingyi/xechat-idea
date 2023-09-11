package cn.xeblog.plugin.game.uno.domain.common;

public interface DomainEventSubscriber {
    void handleEvent(DomainEvent event);
}
