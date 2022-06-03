package cn.xeblog.plugin.game.landlords;

import cn.xeblog.commons.entity.game.landlords.LandlordsGameDTO;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.game.AbstractGame;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author anlingyi
 * @date 2022/6/2 1:13 下午
 */
public class LandlordsGame extends AbstractGame<LandlordsGameDTO> {

    private JPanel startPanel;

    @Override
    protected void init() {
        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
        mainPanel.setMinimumSize(new Dimension(150, 400));
        startPanel = new JPanel();
        startPanel.setBounds(10, 10, 120, 400);
        mainPanel.add(startPanel);

        JLabel title = new JLabel("斗地主！");
        title.setFont(new Font("", 1, 14));
        startPanel.add(title);

        Box vBox = Box.createVerticalBox();
        startPanel.add(vBox);

        vBox.add(Box.createVerticalStrut(20));
        vBox.add(getStartGameButton());
        if (DataCache.isOnline) {
            List<Integer> numsList = new ArrayList();
            numsList.add(2);
            numsList.add(3);
            vBox.add(getCreateRoomButton(numsList));
        }
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    protected void start() {

    }

    @Override
    public void handle(LandlordsGameDTO body) {

    }

    private JButton getStartGameButton() {
        JButton button = new JButton("开始游戏");
        button.addActionListener(e -> start());
        return button;
    }

}
