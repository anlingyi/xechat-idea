package cn.xeblog.plugin.tools.encourage.ui;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.cache.DataCache;
import com.intellij.openapi.ui.ComboBox;
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
import java.util.stream.Collectors;

public abstract class AbstractUI extends JPanel {

    // 设置统一属性
    final Dimension mainDimension = new Dimension(300, 250);
    final Dimension buttonDimension = new Dimension(80, 30);
    final Dimension selectDimension = new Dimension(80, 30);

    protected JLabel buildNewJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setFont(new Font("", Font.BOLD, 13));
        jLabel.setHorizontalAlignment(JLabel.LEFT);
        return jLabel;
    }

    protected ComboBox<String> buildYesOrNoComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMaximumSize(selectDimension);
        comboBox.setPreferredSize(selectDimension);
        comboBox.addItem("是");
        comboBox.addItem("否");
        comboBox.setSelectedItem("否");
        return comboBox;
    }

    private final JTextField searchUserField = new JTextField();
    private final JPanel userListPanel = new JPanel();
    final Box userBox = Box.createVerticalBox();
    final JButton flushButton = new JButton("刷新数据");

    public AbstractUI() {
        super();

        // 搜索框监听事件
        searchUserField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                flushUserList();
            }
        });
        // 刷新按钮点击事件
        flushButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                flushUserList();
            }
        });
        flushButton.setMaximumSize(buttonDimension);

        Box searchBox = Box.createHorizontalBox();
        searchBox.add(new JLabel("搜索用户："));
        searchBox.add(searchUserField);

        JPanel userListMainPanel = new JPanel(new BorderLayout());
        JBScrollPane onlineUserScrollBar = new JBScrollPane(userListPanel);
        onlineUserScrollBar.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        userListMainPanel.setPreferredSize(new Dimension(300, 200));
        userListMainPanel.add(onlineUserScrollBar);

        userBox.add(searchBox);
        userBox.add(Box.createVerticalStrut(5));
        userBox.add(userListMainPanel);
    }

    protected void flushUserList() {
        userListPanel.removeAll();
        Box vBox = Box.createVerticalBox();
        List<User> userList = searchUserList();
        userList.forEach(user -> vBox.add(generateJPanelByUser(user)));
        userListPanel.add(vBox);
        userListPanel.updateUI();
    }

    /**
     * 根据搜索框筛选用户
     *
     * @author 鼓励师
     * @date 2023/10/23 10:15
     */
    protected List<User> searchUserList() {
        String search = searchUserField != null ? searchUserField.getText() : "";
        Map<String, User> map = DataCache.userMap == null ? new HashMap<>() : DataCache.userMap;
        return map.values().stream().filter(u -> StrUtil.isBlank(search) || u.getUsername().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
    }

    /**
     * 生成一个横向的选择面板 用户名 □ □
     *
     * @author 鼓励师
     * @date 2023/10/23 10:14
     */
    protected JPanel generateJPanelByUser(User user) {
        JPanel userPanel = new JPanel();

        JLabel label = new JLabel(user.getUsername());
        label.setForeground(new Color(238, 146, 146));
        label.setFont(new Font("", Font.PLAIN, 13));

        userPanel.add(label);
        userPanel.add(Box.createVerticalStrut(10));
        buildJCheckBoxList(user).forEach(userPanel::add);

        return userPanel;
    }

    /**
     * 构建 N个 选择框
     *
     * @return 选择框集合
     */
    protected List<JCheckBox> buildJCheckBoxList(User user) {
        return new ArrayList<>();
    }
}
