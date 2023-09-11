package cn.xeblog.plugin.game.uno.domain.game;

import cn.xeblog.plugin.game.uno.domain.card.*;
import cn.xeblog.plugin.game.uno.domain.common.DomainEventPublisher;
import cn.xeblog.plugin.game.uno.domain.common.Entity;
import cn.xeblog.plugin.game.uno.domain.game.events.CardDrawn;
import cn.xeblog.plugin.game.uno.domain.game.events.CardPlayed;
import cn.xeblog.plugin.game.uno.domain.game.events.GameOver;
import cn.xeblog.plugin.game.uno.domain.player.ImmutablePlayer;
import cn.xeblog.plugin.game.uno.domain.player.Player;
import cn.xeblog.plugin.game.uno.domain.player.PlayerRoundIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Stream;

public class Game extends Entity {
    private final PlayerRoundIterator players;

    private DrawPile                  drawPile;
    private final Stack<Card>         discardPile = new Stack<>();

    private ImmutablePlayer           winner      = null;

    public Game(DrawPile drawPile, PlayerRoundIterator players) {
        super();
        this.drawPile = drawPile;
        this.players = players;

        startDiscardPile();
    }

    public Stream<ImmutablePlayer> getPlayers() {
        return players.stream().map(Player::toImmutable);
    }

    public ImmutablePlayer getCurrentPlayer() {
        return players.getCurrentPlayer().toImmutable();
    }

    public Stream<Card> getHandCards(UUID playerId) {
        return players.getPlayerById(playerId).getHandCards();
    }

    public Card peekTopCard() {
        return discardPile.peek();
    }

    private void startDiscardPile() {
        var card = drawPile.drawCard();
        switch (card.getType()) {
            case NUMBER:
            case WILD_COLOR:
                discard(card);
                break;
            case SKIP:
                discard(card);
                players.next();
                break;
            case REVERSE:
                discard(card);
                reverse();
                break;
            case DRAW_TWO:
                discard(card);
                drawTwoCards(players.getCurrentPlayer());
                players.next();
                break;
            case WILD_DRAW_FOUR:
                recreateDrawPile(card);
                startDiscardPile();
                break;
            default:
                throw new UnsupportedOperationException("Unsupported card type " + card.getType());
        }
    }

    public void playCard(UUID playerId, Card playedCard) {
        playCard(playerId, playedCard, false);
    }

    public void playCard(UUID playerId, Card playedCard, boolean hasSaidUno) {
        if (isOver()) {
            throw new IllegalStateException("Game is over");
        }

        validatePlayedCard(playerId, playedCard);

        switch (playedCard.getType()) {
            case NUMBER:
                checkNumberCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                players.next();
                break;
            case SKIP:
                checkActionCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                players.next();
                players.next();
                break;
            case REVERSE:
                checkActionCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                reverse();
                break;
            case DRAW_TWO:
                checkActionCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                players.next();
                drawTwoCards(players.getCurrentPlayer());
                players.next();
                break;
            case WILD_COLOR:
                checkWildCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                players.next();
                break;
            case WILD_DRAW_FOUR:
                checkWildCardRule(playedCard);
                acceptPlayedCard(playedCard, hasSaidUno);
                players.next();
                drawFourCards(players.getCurrentPlayer());
                players.next();
                break;
            default:
                rejectPlayedCard(playedCard);
        }

        DomainEventPublisher.publish(new CardPlayed(playerId, playedCard));

        if (isOver()) {
            DomainEventPublisher.publish(new GameOver(winner));
        }
    }

    public void drawCard(UUID playerId) {
        if (getCurrentPlayer().getId().equals(playerId)) {
            var drawnCards = drawCards(players.getCurrentPlayer(), 1);

            tryToPlayDrawnCard(playerId, drawnCards.get(0));
        }
    }

    public boolean isOver() {
        return winner != null;
    }

    public ImmutablePlayer getWinner() {
        return winner;
    }

    private void tryToPlayDrawnCard(UUID playerId, Card drawnCard) {
        try {
            var cardToPlay = CardUtil.isWildCard(drawnCard)
                ? new WildCard(drawnCard.getType(), peekTopCard().getColor())
                : drawnCard;

            playCard(playerId, cardToPlay);
        } catch (Exception ex) {
            // Drawn couldn't be played, so just switch turn
            players.next();
            DomainEventPublisher.publish(new CardDrawn(playerId));
        }
    }

    private void validatePlayedCard(UUID playerId, Card card) {
        var currentPlayer = players.getCurrentPlayer();
        if (!currentPlayer.getId().equals(playerId)) {
            throw new IllegalArgumentException(
                String.format("Not the turn of Player ID %s", playerId));
        }

        if (!currentPlayer.hasHandCard(card)) {
            throw new IllegalArgumentException(
                String.format("Card %s does not exist in player's hand cards", card));
        }
    }

    private void checkNumberCardRule(Card playedCard) {
        var topCard = peekTopCard();

        if (isFirstDiscardAWildCard()
            || CardRules.isValidNumberCard(topCard, (NumberCard) playedCard)) {
            return;
        }

        rejectPlayedCard(playedCard);
    }

    private void checkActionCardRule(Card playedCard) {
        var topCard = peekTopCard();

        if (isFirstDiscardAWildCard()
            || CardRules.isValidActionCard(topCard, (ActionCard) playedCard)) {
            return;
        }

        rejectPlayedCard(playedCard);
    }

    private void checkWildCardRule(Card playedCard) {
        if (!CardRules.isValidWildCard((WildCard) playedCard)) {
            rejectPlayedCard(playedCard);
        }
    }

    private boolean isFirstDiscardAWildCard() {
        return discardPile.size() == 1 && peekTopCard().getType() == CardType.WILD_COLOR;
    }

    private void recreateDrawPile(Card card) {
        if (drawPile.getSize() == 0) {
            throw new IllegalStateException("Not enough cards to recreate draw pile");
        }

        drawPile = DealerService.shuffle(drawPile, card);
    }

    private void acceptPlayedCard(Card card, boolean hasSaidUno) {
        players.getCurrentPlayer().removePlayedCard(card);
        discard(card);

        var remainingTotalCards = getCurrentPlayer().getTotalCards();
        checkSaidUno(remainingTotalCards, hasSaidUno);

        if (remainingTotalCards == 0) {
            winner = getCurrentPlayer();
        }
    }

    private void checkSaidUno(int remainingTotalCards, boolean hasSaidUno) {
        if (remainingTotalCards == 1 && !hasSaidUno) {
            drawCards(players.getCurrentPlayer(), 2);
        }
    }

    private void discard(Card card) {
        discardPile.add(card);
    }

    private void reverse() {
        players.reverseDirection();
        players.next();
    }

    private void drawTwoCards(Player player) {
        drawCards(player, 2);
    }

    private void drawFourCards(Player player) {
        drawCards(player, 4);
    }

    private List<Card> drawCards(Player player, int total) {
        int min = Math.min(total, drawPile.getSize());
        var drawnCards = new ArrayList<Card>();

        for (int i = 0; i < min; i++) {
            var drawnCard = drawPile.drawCard();
            drawnCards.add(drawnCard);

            player.addToHandCards(drawnCard);
        }

        return drawnCards;
    }

    private void rejectPlayedCard(Card playedCard) {
        throw new IllegalArgumentException(
            String.format("Played card %s is not valid for %s", playedCard, peekTopCard()));
    }
}
