package cn.xeblog.plugin.game.uno.domain.player;

import cn.xeblog.plugin.game.uno.domain.card.Card;

import java.io.Serializable;
import java.util.UUID;
import java.util.stream.Stream;

public class ImmutablePlayer implements Serializable {
    private final Player player;

    public ImmutablePlayer(Player player) {
        this.player = player;
    }

    public UUID getId() {
        return player.getId();
    }

    public String getName() {
        return player.getName();
    }

    public Stream<Card> getHandCards() {
        return this.player.getHandCards();
    }

    public int getTotalCards() {
        return (int) getHandCards().count();
    }
}
