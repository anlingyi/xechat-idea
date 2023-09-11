package cn.xeblog.plugin.game.uno.application;

import cn.xeblog.plugin.game.uno.application.dto.PlayerInfoDTO;
import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.player.ImmutablePlayer;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public interface IGameAppService extends Serializable {
    List<PlayerInfoDTO> getPlayerInfos();

    PlayerInfoDTO getCurrentPlayer();

    Stream<Card> getHandCards(UUID playerId);

    void playCard(UUID playerId, Card card, boolean hasSaidUno);

    void drawCard(UUID playerId);

    Card peekTopCard();

    boolean isGameOver();

    ImmutablePlayer getWinner();
}
