package cn.xeblog.plugin.tools.encourage.ui;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.encourage.cache.EncourageCache;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量艾特用户面板
 *
 * @author 鼓励师
 * @date 2023/10/20 13:37
 */
public class AtUsersUI extends AbstractUI {

    public AtUsersUI(JButton backMenu) {
        super();

        Box hBox = Box.createHorizontalBox();
        hBox.add(buildNewJLabel("批量艾特用户"));

        Box mainVBox = Box.createVerticalBox();
        mainVBox.add(hBox);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(userBox);
        mainVBox.add(Box.createVerticalStrut(10));

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 280, 250);
        panel.add(mainVBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(200, 35));
        buttonPanel.add(flushButton);
        buttonPanel.add(getClearJButton());
        buttonPanel.add(backMenu);

        flushUserList();

        this.setMaximumSize(mainDimension);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.updateUI();
    }

    @Override
    protected List<JCheckBox> buildJCheckBoxList(User user) {
        List<JCheckBox> jCheckBoxList = new ArrayList<>();

        boolean anyMatch = EncourageCache.atUsers.stream().anyMatch(u -> u.equals(user));
        JCheckBox blockCheckBox = new JCheckBox("@TA", anyMatch);
        blockCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                EncourageCache.atUsers.add(user);
                ConsoleAction.showSystemMsg(StrUtil.format("增加艾特用户：【{}】", user.getUsername()));
            } else {
                EncourageCache.atUsers.remove(user);
                ConsoleAction.showSystemMsg(StrUtil.format("移除艾特用户：【{}】", user.getUsername()));
            }
        });
        jCheckBoxList.add(blockCheckBox);

        if (EncourageCache.supportPrivateChat) {
            boolean privateCheck = EncourageCache.privateChatUser != null && EncourageCache.privateChatUser.equals(user);
            JCheckBox privateChatCheckBox = new JCheckBox("私聊", privateCheck);
            privateChatCheckBox.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (DataCache.username.equals(user.getUsername())) {
                        ConsoleAction.showSystemMsg(StrUtil.format("可不兴选择自己，换个人吧！"));
                        flushUserList();
                        return;
                    }

                    if (EncourageCache.privateChatUser == null) {
                        ConsoleAction.showSystemMsg(StrUtil.format("设定私聊用户：【{}】", user.getUsername()));
                    } else {
                        ConsoleAction.showSystemMsg(StrUtil.format("切换私聊用户：【{}】->【{}】", EncourageCache.privateChatUser.getUsername(), user.getUsername()));
                    }
                    EncourageCache.privateChatUser = user;
                } else {
                    ConsoleAction.showSystemMsg(StrUtil.format("取消私聊用户：【{}】", user.getUsername()));
                    EncourageCache.privateChatUser = null;
                }
                flushUserList();
            });
            jCheckBoxList.add(privateChatCheckBox);
        }

        return jCheckBoxList;
    }

    public JButton getClearJButton() {
        JButton flushButton = new JButton("清空");
        flushButton.setMaximumSize(buttonDimension);
        flushButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                EncourageCache.atUsers.clear();
                EncourageCache.privateChatUser = null;
                flushUserList();
                ConsoleAction.showSystemMsg("已清空所有艾特用户和私聊用户！");
            }
        });
        return flushButton;
    }
}
