package cn.xeblog.plugin.tools.browser.ui;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * @author anlingyi
 * @date 2023/9/3 3:43 PM
 */
public class SettingUI extends DialogWrapper {

    private JPanel main;

    private JTextField homePageField;

    public SettingUI() {
        super(true);
        setTitle("浏览器设置");
        setResizable(false);
        setOKActionEnabled(true);
        setOKButtonText("确认");
        setCancelButtonText("取消");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        main = new JPanel();
        main.setPreferredSize(new Dimension(250, 50));

        Box hbox = Box.createHorizontalBox();
        hbox.add(new JLabel("主页："));
        homePageField = new JTextField();
        homePageField.setText(DataCache.browserConfig.getHomePage());
        homePageField.setPreferredSize(new Dimension(150, 30));
        hbox.add(homePageField);

        main.add(hbox);
        return main;
    }

    @Override
    protected void doOKAction() {
        String homePage = homePageField.getText();
        if (StrUtil.isNotBlank(homePage)) {
            DataCache.browserConfig.setHomePage(homePage);
        }
        super.doOKAction();
    }

}
