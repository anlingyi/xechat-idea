package cn.xeblog.plugin.game.uno.application;

import cn.xeblog.plugin.game.uno.application.dto.PlayerInfoDTO;
import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.game.Game;
import cn.xeblog.plugin.game.uno.domain.game.GameBuilder;
import cn.xeblog.plugin.game.uno.domain.player.ImmutablePlayer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class GameAppService implements IGameAppService {

    private final Game game;

    public GameAppService() {
        game = new GameBuilder()
            .withPlayer("Player 1")
            .withPlayer("Player 2")
            .build();

        logGameInfo();
    }

    private void logGameInfo() {
        log.info("Game created successfully");
        game.getPlayers().forEach(p -> {
            var joinedCardValues = p.getHandCards()
                .map(Object::toString)
                .collect(Collectors.joining(","));

            log.debug(String.format("Player %s with %s cards => [%s]", p.getName(), p.getTotalCards(), joinedCardValues));
        });
    }

    @Override
    public List<PlayerInfoDTO> getPlayerInfos() {
        return game.getPlayers()
            .map(p -> new PlayerInfoDTO(p.getId(), p.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public PlayerInfoDTO getCurrentPlayer() {
        var currentPlayer = game.getCurrentPlayer();
        return new PlayerInfoDTO(currentPlayer.getId(), currentPlayer.getName());
    }

    @Override
    public Stream<Card> getHandCards(UUID playerId) {
        return game.getHandCards(playerId);
    }

    @Override
    public void playCard(UUID playerId, Card card, boolean hasSaidUno) {
        var message = String.format("Player %s plays %s %s", playerId, card, hasSaidUno ? "and said UNO!!!" : "");
        log.info(message);
        game.playCard(playerId, card, hasSaidUno);
    }

    @Override
    public void drawCard(UUID playerId) {
        var message = String.format("Player %s draws a card", playerId);
        log.info(message);
        game.drawCard(playerId);
    }

    @Override
    public Card peekTopCard() {
        return game.peekTopCard();
    }

    @Override
    public boolean isGameOver() {
        return game.isOver();
    }

    @Override
    public ImmutablePlayer getWinner() {
        return game.getWinner();
    }
}
