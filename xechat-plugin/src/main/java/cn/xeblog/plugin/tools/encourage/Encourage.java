package cn.xeblog.plugin.tools.encourage;

import cn.xeblog.plugin.annotation.DoTool;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.AbstractTool;
import cn.xeblog.plugin.tools.Tools;
import cn.xeblog.plugin.tools.encourage.cache.EncourageCache;
import cn.xeblog.plugin.tools.encourage.ui.AtUsersUI;
import cn.xeblog.plugin.tools.encourage.ui.BlockMessageUI;
import cn.xeblog.plugin.tools.encourage.ui.StatisticsUI;

import javax.swing.*;
import java.awt.*;

/**
 * 附加工具操作面板
 *
 * @author 鼓励师
 * @date 2023/10/20 11:38
 */
@DoTool(Tools.ENCOURAGE)
public class Encourage extends AbstractTool {

    // 设置按钮统一属性
    private final Dimension buttonDimension = new Dimension(100, 30);
    private final Dimension mainDimension = new Dimension(310, 250);

    private JPanel mainPanel;

    @Override
    protected void init() {
        initPanel();

        JLabel title = new JLabel("附加工具面板");
        title.setFont(new Font("", Font.BOLD, 14));

        Box vBox = Box.createVerticalBox();
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(title);
        vBox.add(Box.createVerticalStrut(20));
        vBox.add(newJButton("消息屏蔽", new BlockMessageUI(getMenuJButton())));
        vBox.add(Box.createVerticalStrut(5));
        vBox.add(newJButton("艾特+私聊", new AtUsersUI(getMenuJButton())));

        if (DataCache.getCurrentUser().isAdmin() && EncourageCache.supportStatistics) {
            vBox.add(Box.createVerticalStrut(5));
            vBox.add(newJButton("在线统计", new StatisticsUI(getMenuJButton())));
        }

        vBox.add(Box.createVerticalStrut(5));
        vBox.add(getExitButton());

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 100, 250);
        panel.add(vBox);

        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.updateUI();
    }

    @Override
    protected JComponent getComponent() {
        return mainPanel;
    }

    private void initPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setMaximumSize(mainDimension);
        mainPanel.setMinimumSize(mainDimension);
        mainPanel.setPreferredSize(mainDimension);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }


    /**
     * 统一按钮格式
     */
    private JButton newJButton(String text, JPanel newjPanel) {
        JButton jButton = new JButton(text);
        jButton.setPreferredSize(buttonDimension);

        jButton.addActionListener(j -> {
            initPanel();
            mainPanel.add(newjPanel, BorderLayout.CENTER);
            mainPanel.updateUI();
        });
        return jButton;
    }

    public JButton getMenuJButton() {
        JButton menu = new JButton("主菜单");
        menu.setPreferredSize(buttonDimension);
        menu.addActionListener(e -> init());
        return menu;
    }

}
