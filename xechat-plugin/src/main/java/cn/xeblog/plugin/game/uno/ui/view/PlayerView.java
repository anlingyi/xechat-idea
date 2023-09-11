package cn.xeblog.plugin.game.uno.ui.view;

import cn.xeblog.plugin.game.uno.application.IGameAppService;
import cn.xeblog.plugin.game.uno.application.dto.PlayerInfoDTO;
import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.card.CardType;
import cn.xeblog.plugin.game.uno.domain.card.WildCard;
import cn.xeblog.plugin.game.uno.domain.common.DomainEvent;
import cn.xeblog.plugin.game.uno.domain.common.DomainEventPublisher;
import cn.xeblog.plugin.game.uno.domain.common.DomainEventSubscriber;
import cn.xeblog.plugin.game.uno.domain.game.DealerService;
import cn.xeblog.plugin.game.uno.domain.game.events.CardDrawn;
import cn.xeblog.plugin.game.uno.domain.game.events.CardPlayed;
import cn.xeblog.plugin.game.uno.domain.game.events.GameOver;
import cn.xeblog.plugin.game.uno.ui.common.StyleUtil;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class PlayerView extends JPanel implements DomainEventSubscriber {
    private JLayeredPane handCardsView;
    private Box controlPanel;

    private JLabel nameLabel;
    private JButton drawButton;
    private JButton sayUnoButton;
    private boolean hasSaidUno = false;

    private final PlayerInfoDTO player;

    private final IGameAppService appService;

    public PlayerView(PlayerInfoDTO player, IGameAppService appService) {
        this.player = player;
        this.appService = appService;

        initView();
        DomainEventPublisher.subscribe(this);
    }

    private void initView() {
        Box layout = Box.createHorizontalBox();

        initHandCardsView();
        initControlPanel();

        layout.add(handCardsView);
        layout.add(Box.createHorizontalStrut(20));
        layout.add(controlPanel);
        add(layout);

        setOpaque(false);

        refresh();
    }

    private void initHandCardsView() {
        handCardsView = new JLayeredPane();
        handCardsView.setPreferredSize(new Dimension(600, 175));
        handCardsView.setOpaque(false);
    }

    private void renderHandCardsView() {
        handCardsView.removeAll();

        var handCards = appService.getHandCards(player.getId()).collect(Collectors.toList());

        Point originPoint = getFirstCardPoint(handCards.size());
        int offset = calculateOffset(handCardsView.getWidth(), handCards.size());

        int i = 0;
        for (var card : handCards) {
            var cardView = new CardView(card, this::playCard);

            cardView.setBounds(originPoint.x, originPoint.y,
                cardView.getDimension().width, cardView.getDimension().height);
            handCardsView.add(cardView, i++);
            handCardsView.moveToFront(cardView);

            originPoint.x += offset;
        }

        handCardsView.revalidate();
    }

    private Point getFirstCardPoint(int totalCards) {
        Point p = new Point(0, 20);
        if (totalCards < DealerService.TOTAL_INITIAL_HAND_CARDS) {
            var width = handCardsView.getWidth() == 0 ? handCardsView.getPreferredSize().width : handCardsView.getWidth();

            var offset = calculateOffset(width, totalCards);
            p.x = (width - offset * totalCards) / 2;
        }
        return p;
    }

    private int calculateOffset(int width, int totalCards) {
        if (totalCards <= DealerService.TOTAL_INITIAL_HAND_CARDS) {
            return 71;
        } else {
            return (width - 100) / (totalCards - 1);
        }
    }

    private void initControlPanel() {
        initDrawButton();
        initSayNoButton();
        initNameLabel();

        controlPanel = Box.createVerticalBox();
        controlPanel.add(nameLabel);
        controlPanel.add(drawButton);
        controlPanel.add(Box.createVerticalStrut(15));
        controlPanel.add(sayUnoButton);
    }

    private void toggleControlPanel() {
        var isMyTurn = appService.getCurrentPlayer().getId().equals(player.getId());

        if (appService.isGameOver()) {
            isMyTurn = false;
        }

        drawButton.setVisible(isMyTurn);
        sayUnoButton.setVisible(isMyTurn);

        controlPanel.revalidate();
    }

    private void initNameLabel() {
        nameLabel = new JLabel(player.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font(StyleUtil.DEFAULT_FONT, Font.BOLD, 15));
    }

    private void initSayNoButton() {
        sayUnoButton = new JButton("Say UNO");
        sayUnoButton.setBackground(new Color(149, 55, 53));
        sayUnoButton.setFont(new Font(StyleUtil.DEFAULT_FONT, Font.BOLD, 20));
        sayUnoButton.setFocusable(false);

        sayUnoButton.addActionListener(e -> hasSaidUno = true);
    }

    private void initDrawButton() {
        drawButton = new JButton("Draw");

        drawButton.setBackground(new Color(79, 129, 189));
        drawButton.setFont(new Font(StyleUtil.DEFAULT_FONT, Font.BOLD, 20));
        drawButton.setFocusable(false);

        drawButton.addActionListener(e -> appService.drawCard(player.getId()));
    }

    private void playCard(Card selectedCard) {
        Card cardToPlay = selectedCard;

        if (selectedCard.getType() == CardType.WILD_COLOR || selectedCard.getType() == CardType.WILD_DRAW_FOUR) {
            var chosenColor = ColorPicker.getInstance().show();
            cardToPlay = new WildCard(selectedCard.getType(), chosenColor);
        }

        appService.playCard(player.getId(), cardToPlay, hasSaidUno);
        hasSaidUno = false;
    }

    private void refresh() {
        renderHandCardsView();
        toggleControlPanel();

        repaint();
    }

    @Override
    public void handleEvent(DomainEvent event) {
        if (event instanceof CardPlayed
            || event instanceof CardDrawn
            || event instanceof GameOver) {
            refresh();
        }
    }
}
