package cn.xeblog.plugin.tools.browser.ui;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.tools.browser.config.BrowserConfig;
import cn.xeblog.plugin.tools.browser.core.BrowserEventListener;
import cn.xeblog.plugin.tools.browser.core.BrowserService;
import cn.xeblog.plugin.tools.browser.core.JcefBrowserService;
import cn.xeblog.plugin.tools.browser.core.UserAgent;
import com.intellij.openapi.ui.ComboBox;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author anlingyi
 * @date 2022/8/14 11:48 AM
 */
public class BrowserUI extends JPanel {

    private BrowserService browserService;

    private String lastUrl;

    private WindowMode windowMode;

    private UserAgent userAgent;

    private Component browserUI;

    private JTextField urlField;

    private BrowserConfig browserConfig;

    private enum WindowMode {
        SMALL("S", 200, 250),
        MEDIUM("M", 400, 300);

        @Getter
        String name;

        @Getter
        int width;

        @Getter
        int height;

        WindowMode(String name, int width, int height) {
            this.name = name;
            this.width = width;
            this.height = height;
        }

        public static WindowMode getMode(String name) {
            for (WindowMode mode : values()) {
                if (mode.getName().equals(name)) {
                    return mode;
                }
            }
            return WindowMode.SMALL;
        }
    }

    public BrowserUI() {
        this.windowMode = WindowMode.SMALL;
        this.userAgent = UserAgent.IPHONE;
        this.browserConfig = DataCache.browserConfig;
        initPanel();
    }

    private void initPanel() {
        removeAll();

        String url = browserConfig.getHomePage();
        if (lastUrl != null) {
            url = lastUrl;
        }

        if (this.browserService != null) {
            this.browserService.close();
        }
        this.browserService = new JcefBrowserService(url);
        this.browserService.setUserAgent(userAgent);

        browserUI = browserService.getUI();
        urlField = new JTextField(url);
        resize();

        Dimension buttonDimension = new Dimension(50, 25);
        Box hbox = Box.createHorizontalBox();

        JButton exitButton = new JButton("✕");
        exitButton.setToolTipText("退出");
        exitButton.setPreferredSize(buttonDimension);
        exitButton.addActionListener(l -> Command.OVER.exec());
        hbox.add(exitButton);

        JButton settingButton = new JButton("✡");
        settingButton.setToolTipText("设置");
        settingButton.setPreferredSize(buttonDimension);
        settingButton.addActionListener(l -> new SettingUI().show());
        hbox.add(settingButton);

        JButton homeButton = new JButton("♨");
        homeButton.setToolTipText("主页");
        homeButton.setPreferredSize(buttonDimension);
        homeButton.addActionListener(l -> browserService.loadURL(browserConfig.getHomePage()));
        hbox.add(homeButton);

        JButton backButton = new JButton("←");
        backButton.setToolTipText("后退");
        backButton.setPreferredSize(buttonDimension);
        backButton.addActionListener(l -> browserService.goBack());
        hbox.add(backButton);

        JButton forwardButton = new JButton("→");
        forwardButton.setToolTipText("前进");
        forwardButton.setPreferredSize(buttonDimension);
        forwardButton.addActionListener(l -> browserService.goForward());
        hbox.add(forwardButton);

        JButton refreshButton = new JButton("⟳");
        refreshButton.setToolTipText("刷新");
        refreshButton.setPreferredSize(buttonDimension);
        refreshButton.addActionListener(l -> browserService.reload());
        hbox.add(refreshButton);
        hbox.add(urlField);

        JPanel urlPanel = new JPanel();
        urlPanel.add(hbox);

        Box h2Box = Box.createHorizontalBox();
        h2Box.add(new JLabel("Window："));
        ComboBox windowModeBox = new ComboBox();
        windowModeBox.setPreferredSize(buttonDimension);
        for (WindowMode mode : WindowMode.values()) {
            windowModeBox.addItem(mode.getName());
        }
        windowModeBox.setSelectedItem(windowMode.getName());
        windowModeBox.addItemListener(l -> {
            windowMode = WindowMode.getMode(l.getItem().toString());
            resize();
            updateUI();
        });
        h2Box.add(windowModeBox);

        h2Box.add(Box.createHorizontalStrut(5));
        h2Box.add(new JLabel("UA："));
        ComboBox uaBox = new ComboBox();
        uaBox.setPreferredSize(new Dimension(100, 30));
        for (UserAgent userAgent : UserAgent.values()) {
            uaBox.addItem(userAgent.getName());
        }
        uaBox.setSelectedItem(userAgent.getName());
        uaBox.addItemListener(l -> {
            userAgent = UserAgent.getUserAgent(l.getItem().toString());
            browserService.setUserAgent(userAgent);
            browserService.reload();
        });
        h2Box.add(uaBox);

        h2Box.add(Box.createHorizontalStrut(5));
        h2Box.add(new JLabel("Zoom："));
        JTextField zoomLevelField = new JTextField("0.0");
        zoomLevelField.setPreferredSize(new Dimension(50, 30));
        zoomLevelField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String value = zoomLevelField.getText();
                    if (NumberUtil.isNumber(value)) {
                        double zoom = Double.parseDouble(value);
                        browserService.setZoomLevel(zoom);
                    }
                }
            }
        });
        h2Box.add(zoomLevelField);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(h2Box);

        setLayout(new BorderLayout());
        add(urlPanel, BorderLayout.NORTH);
        add(browserUI, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        updateUI();

        urlField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String url = urlField.getText();
                    if (!StrUtil.startWithAny(url, "https://", "http://")) {
                        url = "https://" + url;
                    }
                    browserService.loadURL(url);
                }
            }
        });

        this.browserService.addEventListener(new BrowserEventListener() {
            @Override
            public void onAddressChange(String url) {
                lastUrl = url;
                urlField.setText(url);
            }

            @Override
            public void onBeforeClose() {
                SwingUtilities.invokeLater(() -> initPanel());
            }
        });
    }

    private void resize() {
        int width = this.windowMode.getWidth();
        int height = this.windowMode.getHeight();

        urlField.setPreferredSize(new Dimension(width * 2 / 3, 30));
        urlField.updateUI();

        Dimension browserDimension = new Dimension(width, height);
        browserUI.setMinimumSize(null);
        browserUI.setPreferredSize(null);
        if (windowMode == WindowMode.SMALL) {
            browserUI.setPreferredSize(browserDimension);
        } else {
            browserUI.setMinimumSize(browserDimension);
        }
    }

    public void close() {
        this.browserService.close();
    }

}
