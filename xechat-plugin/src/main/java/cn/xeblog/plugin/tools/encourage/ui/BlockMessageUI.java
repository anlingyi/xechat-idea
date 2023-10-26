package cn.xeblog.plugin.tools.encourage.ui;

import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.encourage.cache.EncourageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

/**
 * 消息屏蔽面板
 *
 * @author 鼓励师
 * @date 2023/10/20 13:37
 */
public class BlockMessageUI extends AbstractUI {

    final JCheckBox tipsCheckBox = new JCheckBox("显示用户消息提示");

    public BlockMessageUI(JButton backMenu) {
        super();

        Box hBox = Box.createHorizontalBox();
        hBox.add(buildNewJLabel("用户消息屏蔽"));

        Box tipsBox = Box.createHorizontalBox();
        tipsBox.add(tipsCheckBox);
        tipsCheckBox.addItemListener(e -> {
            EncourageCache.showTips = e.getStateChange() == ItemEvent.SELECTED;
            ConsoleAction.showSystemMsg(String.format("[%s]屏蔽用户的消息提示", EncourageCache.showTips ? "开启" : "关闭"));
            flushUserList();
        });

        Box mainVBox = Box.createVerticalBox();
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(hBox);
        mainVBox.add(Box.createVerticalStrut(5));
        mainVBox.add(tipsBox);
        mainVBox.add(Box.createVerticalStrut(5));
        mainVBox.add(userBox);

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 200, 250);
        panel.add(mainVBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(200, 35));
        buttonPanel.add(getClearJButton());
        buttonPanel.add(flushButton);
        buttonPanel.add(backMenu);

        flushUserList();

        this.setMaximumSize(mainDimension);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.updateUI();
    }

    public JButton getClearJButton() {
        JButton flushButton = new JButton("清空所有");
        flushButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EncourageCache.BLOCK_USER_CACHE.clear();
                flushUserList();
                ConsoleAction.showSystemMsg("已清空所有屏蔽的用户信息！");
            }
        });
        return flushButton;
    }

    private void doBlock(User user, boolean selected) {
        String username = user.getUsername();
        String uuid = user.getUuid();

        if (DataCache.username.equals(username) && DataCache.uuid.equals(uuid)) {
            ConsoleAction.showSystemMsg("这么想不开要屏蔽自己？不批！");
            flushUserList();
            return;
        }

        if (selected) {
            EncourageCache.BLOCK_USER_CACHE.add(username);
            EncourageCache.BLOCK_USER_CACHE.add(uuid);
            ConsoleAction.showSystemMsg(String.format("已屏蔽用户[%s]的消息", username));
        } else {
            EncourageCache.BLOCK_USER_CACHE.remove(username);
            EncourageCache.BLOCK_USER_CACHE.remove(uuid);
            ConsoleAction.showSystemMsg(String.format("已解除屏蔽用户[%s]的消息", username));
        }
    }

    @Override
    protected List<JCheckBox> buildJCheckBoxList(User user) {
        boolean anyMatch = EncourageCache.BLOCK_USER_CACHE.stream().anyMatch(b -> b.equals(user.getUsername()) || b.equals(user.getUuid()));
        JCheckBox blockCheckBox = new JCheckBox("屏蔽", anyMatch);
        blockCheckBox.addItemListener(e -> doBlock(user, e.getStateChange() == ItemEvent.SELECTED));
        return Collections.singletonList(blockCheckBox);
    }
}
