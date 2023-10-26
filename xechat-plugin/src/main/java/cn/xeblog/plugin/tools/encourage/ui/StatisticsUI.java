package cn.xeblog.plugin.tools.encourage.ui;

import cn.xeblog.commons.entity.StatisticsMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.commons.enums.CountEnum;
import cn.xeblog.plugin.action.MessageAction;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

/**
 * 在线统计面板
 *
 * @author 鼓励师
 * @date 2023/10/20 13:37
 */
public class StatisticsUI extends AbstractUI {

    private CountEnum selectCountEnum;
    private boolean toAll = false;
    private boolean showDetail = false;
    private int showSize = 6;
    private final ComboBox<String> showSizeComboBox = new ComboBox<>();

    public StatisticsUI(JButton backMenu) {

        buildShowSizeComboBox();

        Box hBox = Box.createHorizontalBox();
        hBox.add(buildNewJLabel("在线统计"));

        Box selectBox1 = Box.createHorizontalBox();
        selectBox1.add(buildNewJLabel("统计分类："));
        selectBox1.add(buildCountComboBox());

        ComboBox<String> yesOrNoComboBox1 = buildYesOrNoComboBox();
        yesOrNoComboBox1.addActionListener(l -> toAll = "是".equals(yesOrNoComboBox1.getSelectedItem()));
        Box selectBox2 = Box.createHorizontalBox();
        selectBox2.add(buildNewJLabel("所有人可见："));
        selectBox2.add(yesOrNoComboBox1);

        ComboBox<String> yesOrNoComboBox2 = buildYesOrNoComboBox();
        yesOrNoComboBox2.addActionListener(l -> {
            showDetail = "是".equals(yesOrNoComboBox2.getSelectedItem());
            showSizeComboBox.setEnabled(showDetail);
            showSizeComboBox.repaint();
        });
        Box selectBox3 = Box.createHorizontalBox();
        selectBox3.add(buildNewJLabel("是否展示详情："));
        selectBox3.add(yesOrNoComboBox2);

        Box selectBox4 = Box.createHorizontalBox();
        selectBox4.add(buildNewJLabel("表格展示列："));
        selectBox4.add(showSizeComboBox);

        Box mainVBox = Box.createVerticalBox();
        mainVBox.add(hBox);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(selectBox1);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(selectBox2);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(selectBox3);
        mainVBox.add(Box.createVerticalStrut(10));
        mainVBox.add(selectBox4);
        mainVBox.add(Box.createVerticalStrut(10));

        JPanel panel = new JPanel();
        panel.setBounds(10, 10, 200, 250);
        panel.add(mainVBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(150, 70));
        buttonPanel.add(bulidSendJButton());
        buttonPanel.add(backMenu);

        this.setMaximumSize(mainDimension);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        this.updateUI();
    }

    private ComboBox<String> buildCountComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setMaximumSize(selectDimension);
        comboBox.setPreferredSize(selectDimension);
        for (CountEnum countEnum : CountEnum.values()) {
            comboBox.addItem(countEnum.getDesc());
        }
        comboBox.addActionListener(l -> selectCountEnum = CountEnum.getByIndex(comboBox.getSelectedIndex()));
        comboBox.setSelectedItem(CountEnum.CITY.getDesc());
        return comboBox;
    }

    private void buildShowSizeComboBox() {
        showSizeComboBox.setMaximumSize(selectDimension);
        showSizeComboBox.setPreferredSize(selectDimension);
        for (int i = 5; i <= 9; i++) {
            showSizeComboBox.addItem(String.valueOf(i));
        }
        showSizeComboBox.setSelectedItem(String.valueOf(showSize));
        showSizeComboBox.addActionListener(l -> showSize = Integer.parseInt(String.valueOf(showSizeComboBox.getSelectedItem())));
        showSizeComboBox.setEnabled(showDetail);
    }

    private JButton bulidSendJButton() {
        JButton menu = new JButton("发送");
        menu.setMaximumSize(buttonDimension);
        menu.addActionListener(e -> send());
        return menu;
    }

    private void send() {
        StatisticsMsgDTO statisticsMsgDTO = new StatisticsMsgDTO();
        statisticsMsgDTO.setCountEnum(selectCountEnum);
        statisticsMsgDTO.setToAll(toAll);
        statisticsMsgDTO.setShowDetail(showDetail);
        statisticsMsgDTO.setShowSize(showSize);
        MessageAction.send(statisticsMsgDTO, Action.STATISTICS);
    }

}
