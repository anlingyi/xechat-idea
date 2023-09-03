package cn.xeblog.plugin.tools.read.page;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.tools.read.util.KeyFormatUtil;
import cn.xeblog.plugin.util.AlertMessagesUtil;
import com.intellij.ui.components.JBLoadingPanel;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

/**
 * @author LYF
 * @date 2022-07-18
 */
public class SettingPage implements IPage {

    private JBLoadingPanel settingPanel;
    private JBTable configTable;
    private boolean modified = false;

    @Override
    public void show() {
        if (settingPanel == null) {
            initUI();
        }
        initConfigTableData();
        cn.xeblog.plugin.tools.read.page.UIManager.showPage(settingPanel, 250, 200);
    }

    @Override
    public void initUI() {
        settingPanel = new JBLoadingPanel(new BorderLayout(), DataCache.project);
        settingPanel.setBounds(10, 10, 230, 200);

        configTable = new JBTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1 && row > -1 && row < 2) {
                    modified = true;
                    return true;
                }
                return false;
            }
        };
        addTableKeyListener();
        settingPanel.add(new JScrollPane(configTable), BorderLayout.CENTER);

        JPanel optPanel = new JPanel();
        optPanel.add(getSaveButton());
        optPanel.add(getBackButton());
        settingPanel.add(optPanel, BorderLayout.SOUTH);
    }

    /**
     * 初始化配置数据
     */
    private void initConfigTableData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("名称", new String[]{
                "Legado Host", "困难模式单行字数",
                "上一页热键", "下一页热键", "老板键", "恢复键",
                "自动翻页速度(秒)", "自动翻页开始", "自动翻页停止"
        });
        String[] values = new String[2];
        values[0] = DataCache.readConfig.getLegadoHost();
        values[1] = String.valueOf(DataCache.readConfig.getHardColumns());
        values = ArrayUtil.append(values, DataCache.readConfig.getKey());
        String pageTurningSpeed = String.valueOf(DataCache.readConfig.getPageTurningSpeed());
        model.addColumn("值", ArrayUtil.insert(values, 6, pageTurningSpeed));
        configTable.setModel(model);
    }

    /**
     * 添加键盘事件
     */
    private void addTableKeyListener() {
        for (KeyListener listener : configTable.getKeyListeners()) {
            configTable.removeKeyListener(listener);
        }
        configTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int row = configTable.getSelectedRow();
                if (row < 2) {
                    return;
                }
                String key = KeyFormatUtil.format(e);
                if ("".equals(key)) {
                    return;
                }
                modified = true;
                configTable.setValueAt(key, row, 1);
                super.keyPressed(e);
            }
        });
    }

    /**
     * 保存按钮
     */
    private JButton getSaveButton() {
        JButton saveButton = new JButton("保存");
        saveButton.addActionListener(e -> {
            settingPanel.startLoading();
            for (Component component : settingPanel.getContentPanel().getComponents()) {
                component.setVisible(false);
            }
            ThreadUtil.execute(() -> {
                try {
                    if (saveConfig()) {
                        modified = false;
                        SwingUtilities.invokeLater(() -> cn.xeblog.plugin.tools.read.page.UIManager.startPage.show());
                    }
                } finally {
                    for (Component component : settingPanel.getContentPanel().getComponents()) {
                        component.setVisible(true);
                    }
                    settingPanel.stopLoading();
                }
            });
        });
        return saveButton;
    }

    /**
     * 返回按钮
     */
    private JButton getBackButton() {
        JButton backButton = new JButton("返回");
        backButton.addActionListener(e -> {
            if (!modified || AlertMessagesUtil.showYesNoDialog("提示", "直接返回将不会保存修改")) {
                modified = false;
                UIManager.startPage.show();
            }
        });
        return backButton;
    }

    /**
     * 保存配置
     * @return 是否保存成功
     */
    private boolean saveConfig() {
        String host = (String) configTable.getValueAt(0, 1);
        if (StrUtil.isNotEmpty(host) && !StrUtil.equals(DataCache.readConfig.getLegadoHost(), host)) {
            String ip;
            if (host.contains(":")) {
                String[] hostInfo = host.split(":");
                ip = hostInfo[0];
                if (hostInfo.length < 2) {
                    host = ip;
                }
            } else {
                ip = host;
            }
            if (DataCache.readConfig.verifyLegadoHost(ip)) {
                DataCache.readConfig.setLegadoHost(host);
            } else {
                SwingUtilities.invokeLater(() -> {
                    AlertMessagesUtil.showErrorDialog("错误", "请设置正确的Legado Host");
                });
                return false;
            }
        }
        try {
            int hardColumns = NumberUtil.parseInt((String) configTable.getValueAt(1, 1));
            if (hardColumns < 1 || hardColumns > 150) {
                throw new Exception();
            } else {
                DataCache.readConfig.setHardColumns(hardColumns);
            }
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                AlertMessagesUtil.showErrorDialog("错误", "困难模式单行字数必须是一个1~150的数字");
            });
            return false;
        }
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(2, 1), 0);
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(3, 1), 1);
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(4, 1), 2);
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(5, 1), 3);
        try {
            int pageTurningSpeed = NumberUtil.parseInt((String) configTable.getValueAt(6, 1));
            if (pageTurningSpeed < 1 || pageTurningSpeed > 60) {
                throw new Exception();
            } else {
                DataCache.readConfig.setPageTurningSpeed(pageTurningSpeed);
            }
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                AlertMessagesUtil.showErrorDialog("错误", "自动翻译速度必须是一个1~60的数字");
            });
            return false;
        }
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(7, 1), 4);
        DataCache.readConfig.setKeyMap((String) configTable.getValueAt(8, 1), 5);
        return true;
    }
}
