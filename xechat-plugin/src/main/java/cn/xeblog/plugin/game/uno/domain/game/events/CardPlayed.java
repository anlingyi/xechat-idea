package cn.xeblog.plugin.game.uno.domain.game.events;

import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.common.DomainEvent;

import java.util.UUID;

public class CardPlayed extends DomainEvent {
    private final UUID playerId;
    private final Card playedCard;

    public CardPlayed(UUID playerId, Card playedCard) {
        this.playerId = playerId;
        this.playedCard = playedCard;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Card getPlayedCard() {
        return playedCard;
    }
}
