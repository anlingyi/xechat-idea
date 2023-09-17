package cn.xeblog.plugin.game.box;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.enums.Game;
import cn.xeblog.plugin.annotation.DoGame;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.game.box.util.ImagesUtils;
import cn.xeblog.plugin.game.box.util.MapsUtils;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * 功能描述: 数独主方法 D:\代码工程\project\test
 *
 * @author ☆程序员鼓励师☆
 * @date 2022年8月20日01:02:27
 */
@DoGame(Game.PUSH_BOX)
public class PushBox extends AbstractGame {

    private JPanel mainPanel;
    private int level;
    private boolean init;
    private PushBoxUI pushBoxUI;

    @Override
    protected void start() {
        initPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        mainPanel.add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        if (!init && pushBoxUI != null) {
            level = pushBoxUI.getLevel();
        }
        pushBoxUI = new PushBoxUI(level);
        mainPanel.add(pushBoxUI, BorderLayout.CENTER);
        mainPanel.add(getBottomPanel(), BorderLayout.SOUTH);

        mainPanel.setMinimumSize(new Dimension(pushBoxUI.getTheWidth() + 40, pushBoxUI.getTheHeight() + 50));
        mainPanel.updateUI();

        pushBoxUI.requestFocusInWindow();
        init = false;
    }

    @Override
    protected void init() {
        // 初始化图片
        ImagesUtils.initMapDataDefault();
        // 初始化地图
        MapsUtils.initMapDataDefault();
        // 是否初始化
        init = true;
        level = 1;

        initPanel();

        mainPanel.setMinimumSize(new Dimension(150, 300));
        JPanel menuJPanel = new JPanel();
        menuJPanel.setBounds(10, 10, 100, 330);
        mainPanel.add(menuJPanel);

        JLabel title = new JLabel("推箱子");
        title.setFont(new Font("", Font.BOLD, 14));
        menuJPanel.add(title);

        Box vBox = Box.createVerticalBox();
        menuJPanel.add(vBox);

        Dimension selectDimension = new Dimension(30, 30);

        vBox.add(Box.createVerticalStrut(20));
        JLabel levelLabel = new JLabel("关卡选择：");
        levelLabel.setFont(new Font("", Font.BOLD, 13));
        vBox.add(levelLabel);
        vBox.add(Box.createVerticalStrut(5));

        ComboBox<String> gameLevelBox = getComboBox(selectDimension);
        gameLevelBox.addActionListener(l -> level = gameLevelBox.getSelectedIndex() + 1);
        vBox.add(gameLevelBox);

        vBox.add(Box.createVerticalStrut(10));
        vBox.add(getStartJButton("开始游戏"));
        vBox.add(getTipsButton());
        vBox.add(getExitButton());

        mainPanel.updateUI();
    }

    @Override
    protected JPanel getComponent() {
        return mainPanel;
    }

    protected void initPanel() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
        }

        mainPanel.removeAll();
        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(null);
        mainPanel.setEnabled(true);
        mainPanel.setVisible(true);
    }

    // 创建按钮面板
    private JPanel getBottomPanel() {
        JPanel jPanel = new JPanel();
        jPanel.add(getStartJButton("重置本关"));
        jPanel.add(getMenuJButton());
        return jPanel;
    }

    public JButton getMenuJButton() {
        JButton menu = new JButton("主菜单");
        menu.addActionListener(e -> init());
        return menu;
    }

    public JButton getTipsButton() {
        JButton tips = new JButton("按键提示");
        tips.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String message = "↑ ↓ ← → 方向键控制人物移动方向<br>";
                switch (e.getButton()) {
                    case 1:
                        message += "DELETE：撤销/上一步<br>祝你玩得愉快！";
                        break;
                    case 2:
                        message += "DELETE：撤销/上一步<br>隐藏技能都被你发现了！真棒！<br>PAGE UP：上一关<br>PAGE DOWN：下一关<br>";
                        break;
                    default:
                        break;
                }
                message = StrUtil.format("<html><body>{}<body></html>", message);
                JOptionPane.showMessageDialog(null, message, "按键提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return tips;
    }

    public JButton getStartJButton(String title) {
        JButton another = new JButton(title);
        another.addActionListener(e -> start());
        return another;
    }

    public ComboBox<String> getComboBox(Dimension dimension) {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPreferredSize(dimension);
        for (int i = 1; i <= MapsUtils.getTotal(); i++) {
            comboBox.addItem(StrUtil.format("第{}关", i));
        }
        comboBox.setSelectedItem(StrUtil.format("第{}关", level));
        return comboBox;
    }
}
