package cn.xeblog.plugin.game.uno.domain.game.events;

import cn.xeblog.plugin.game.uno.domain.common.DomainEvent;

import java.util.UUID;

public class CardDrawn extends DomainEvent {
    private final UUID playerId;

    public CardDrawn(UUID playerId){
        this.playerId = playerId;
    }

    public UUID getPlayerId() {
        return playerId;
    }
}
