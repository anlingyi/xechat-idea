package cn.xeblog.plugin.ui;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.AdminReact;
import cn.xeblog.commons.entity.react.result.AdminReactResult;
import cn.xeblog.commons.enums.Permissions;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.ReactAction;
import cn.xeblog.plugin.action.handler.ReactResultConsumer;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.AbstractPanelComponent;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anlingyi
 * @date 2023/2/18 8:49 PM
 */
public class AdminUI extends AbstractPanelComponent {

    public AdminUI() {
        super(true);
    }

    private JPanel mainPanel;

    private JTextField searchUserField;

    private JPanel userListPanel;

    private JCheckBox globalCheckBox1;

    private JCheckBox globalCheckBox2;

    private JTextField maxFileSizeField;

    private AdminReactResult data;

    @Override
    protected void init() {
        mainPanel = new JPanel();

        globalCheckBox1 = new JCheckBox("全员禁言");
        globalCheckBox2 = new JCheckBox("全员禁图");
        Box hBox = Box.createHorizontalBox();
        hBox.add(globalCheckBox1);
        hBox.add(Box.createHorizontalStrut(5));
        hBox.add(globalCheckBox2);

        JLabel label1 = new JLabel("文件大小限制(单位：KB)");
        maxFileSizeField = new JTextField();
        maxFileSizeField.setMaximumSize(new Dimension(80, 30));
        Box hBox2 = Box.createHorizontalBox();
        hBox2.add(label1);
        hBox2.add(maxFileSizeField);

        JLabel label2 = new JLabel("搜索用户：");
        searchUserField = new JTextField();

        Box hBox3 = Box.createHorizontalBox();
        hBox3.add(label2);
        hBox3.add(searchUserField);
        Box vBox2 = Box.createVerticalBox();
        vBox2.add(hBox3);
        vBox2.add(Box.createVerticalStrut(5));

        JPanel userListMainPanel = new JPanel(new BorderLayout());
        userListMainPanel.setPreferredSize(new Dimension(280, 250));

        userListPanel = new JPanel();
        flushUserList();
        JBScrollPane onlineUserScrollBar = new JBScrollPane(userListPanel);
        onlineUserScrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        userListMainPanel.add(onlineUserScrollBar);
        vBox2.add(userListMainPanel);

        Box mainVBox = Box.createVerticalBox();
        mainVBox.add(hBox);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(hBox2);
        mainVBox.add(Box.createVerticalStrut(20));
        mainVBox.add(vBox2);
        mainVBox.add(Box.createVerticalStrut(10));

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 280, 400);
        panel.add(mainVBox);

        JPanel buttonPanel = new JPanel();
        JButton flushButton = new JButton("刷新数据");
        flushButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                queryData();
            }
        });

        JButton exitButton = getExitButton();
        exitButton.setText("退出管控");

        buttonPanel.add(flushButton);
        buttonPanel.add(exitButton);

        bindEvent();
        queryData();

        mainPanel.setMinimumSize(new Dimension(300, 420));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.updateUI();
    }

    @Override
    protected JComponent getComponent() {
        return mainPanel;
    }

    private void bindEvent() {
        globalCheckBox1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean selected = globalCheckBox1.isSelected();
                AdminReact.Operate operate = selected ? AdminReact.Operate.GLOBAL_PERMIT_REMOVE : AdminReact.Operate.GLOBAL_PERMIT_ADD;
                sendRequest(new AdminReact(operate, Permissions.SPEAK));
            }
        });

        globalCheckBox2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                boolean selected = globalCheckBox2.isSelected();
                AdminReact.Operate operate = selected ? AdminReact.Operate.GLOBAL_PERMIT_REMOVE : AdminReact.Operate.GLOBAL_PERMIT_ADD;
                sendRequest(new AdminReact(operate, Permissions.SEND_FILE));
            }
        });

        maxFileSizeField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String text = maxFileSizeField.getText();
                    if (StrUtil.isBlank(text)) {
                        return;
                    }

                    if (NumberUtil.isNumber(text)) {
                        sendRequest(new AdminReact(AdminReact.Operate.GLOBAL_MAX_FILE_SIZE, text));
                    }
                }
            }
        });

        searchUserField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                flushUserList();
            }
        });
    }

    private void flushUserList() {
        Map<String, User> onlineUserMap = DataCache.userMap;
        if (onlineUserMap == null) {
            onlineUserMap = new HashMap<>();
        }

        List<User> userList = new ArrayList<>();
        String search = searchUserField != null ? searchUserField.getText() : "";
        if (StrUtil.isBlank(search)) {
            userList.addAll(onlineUserMap.values());
        } else {
            onlineUserMap.forEach((k, v) -> {
                if (k.toLowerCase().contains(search.toLowerCase())) {
                    userList.add(v);
                }
            });
        }

        userListPanel.removeAll();
        Box vBox = Box.createVerticalBox();
        for (User user : userList) {
            JPanel userPanel = new JPanel();

            JLabel label = new JLabel(user.getUsername());
            label.setForeground(new Color(238, 146, 146));
            label.setFont(new Font("", 0, 13));

            JCheckBox checkBox1 = new JCheckBox("禁言", !user.hasPermit(Permissions.SPEAK));
            JCheckBox checkBox2 = new JCheckBox("禁图", !user.hasPermit(Permissions.SEND_FILE));

            checkBox1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean selected = checkBox1.isSelected();
                    AdminReact.Operate operate = selected ? AdminReact.Operate.USER_PERMIT_REMOVE : AdminReact.Operate.USER_PERMIT_ADD;
                    sendRequest(new AdminReact(operate, Permissions.SPEAK, user.getId()));
                }
            });

            checkBox2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    boolean selected = checkBox2.isSelected();
                    AdminReact.Operate operate = selected ? AdminReact.Operate.USER_PERMIT_REMOVE : AdminReact.Operate.USER_PERMIT_ADD;
                    sendRequest(new AdminReact(operate, Permissions.SEND_FILE, user.getId()));
                }
            });

            userPanel.add(label);
            userPanel.add(Box.createVerticalStrut(10));
            userPanel.add(checkBox1);
            userPanel.add(checkBox2);

            vBox.add(userPanel);
        }

        userListPanel.add(vBox);
        userListPanel.updateUI();
    }

    private void sendRequest(AdminReact react) {
        ReactAction.request(react, React.ADMIN, new ReactResultConsumer<AdminReactResult>() {
            @Override
            public void doSucceed(AdminReactResult body) {
                if (body != null) {
                    data = body;
                }

                flushData();
            }

            @Override
            public void doFailed(String msg) {
                flushData();
                ConsoleAction.showSimpleMsg("[管控]数据更新失败！原因：" + msg);
            }
        });
    }

    private void queryData() {
        AdminReact react = new AdminReact();
        react.setOperate(AdminReact.Operate.QUERY_PERMIT);
        sendRequest(react);
    }

    private void flushData() {
        if (data == null) {
            return;
        }

        int globalPermit = data.getGlobalPermit();
        globalCheckBox1.setSelected(!Permissions.SPEAK.hasPermit(globalPermit));
        globalCheckBox2.setSelected(!Permissions.SEND_FILE.hasPermit(globalPermit));
        maxFileSizeField.setText(String.valueOf(data.getMaxFileSize()));
        invoke(this::flushUserList);
    }

}
