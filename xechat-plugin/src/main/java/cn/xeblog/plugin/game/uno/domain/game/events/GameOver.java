package cn.xeblog.plugin.game.uno.domain.game.events;

import cn.xeblog.plugin.game.uno.domain.common.DomainEvent;
import cn.xeblog.plugin.game.uno.domain.player.ImmutablePlayer;

public class GameOver extends DomainEvent {
    private ImmutablePlayer winner;

    public GameOver(ImmutablePlayer winner) {
        this.winner = winner;
    }

    public ImmutablePlayer getWinner() {
        return winner;
    }
}
