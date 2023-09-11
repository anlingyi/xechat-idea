package cn.xeblog.plugin.game.uno.ui.view;

import cn.xeblog.plugin.game.uno.application.IGameAppService;
import cn.xeblog.plugin.game.uno.domain.card.Card;
import cn.xeblog.plugin.game.uno.domain.common.DomainEvent;
import cn.xeblog.plugin.game.uno.domain.common.DomainEventPublisher;
import cn.xeblog.plugin.game.uno.domain.common.DomainEventSubscriber;
import cn.xeblog.plugin.game.uno.domain.game.events.CardPlayed;
import cn.xeblog.plugin.game.uno.ui.common.StyleUtil;

import javax.swing.*;
import java.awt.*;

public class TableView extends JPanel implements DomainEventSubscriber {
    private final JPanel table;
    private final IGameAppService appService;

    public TableView(IGameAppService appService){
        this.appService = appService;

        setOpaque(false);
        setLayout(new GridBagLayout());
        table = new JPanel();
        table.setBackground(new Color(64,64,64));

        initTable();
        initInfoView();

        DomainEventPublisher.subscribe(this);
    }

    private void initTable(){
        table.removeAll();

        table.setPreferredSize(new Dimension(500,200));
        table.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        Card topCard = appService.peekTopCard();
        Color background = StyleUtil.convertCardColor(topCard.getColor());

        var cardView = new CardView(topCard);
        table.add(cardView, c);

        table.setBackground(background);
        table.revalidate();
    }

    private void initInfoView() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 130, 0, 45);
        add(table,c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_END;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(0, 1, 0, 1);

        add(new GameStatusView(appService), c);
    }

    @Override
    public void handleEvent(DomainEvent event) {
        if(event instanceof CardPlayed) {
            initTable();
        }
    }
}
