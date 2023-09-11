package cn.xeblog.plugin.game.uno.domain.common;

import java.time.LocalDate;

public abstract class DomainEvent {
    private final LocalDate occurredDate;

    protected DomainEvent(){
        occurredDate = LocalDate.now();
    }

    public LocalDate getOccurredDate(){
        return occurredDate;
    }
}
