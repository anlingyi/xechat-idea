package cn.xeblog.plugin.tools.browser.ui;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.enums.Command;
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

    private final static String HOME_PAGE = "https://cn.bing.com";

    private BrowserService browserService;

    private String lastUrl;

    private WindowMode windowMode;

    private enum WindowMode {
        SMALL("S", UserAgent.IPHONE, 200, 250),
        MEDIUM("M", UserAgent.IPAD, 400, 300);

        @Getter
        String name;

        @Getter
        UserAgent userAgent;

        @Getter
        int width;

        @Getter
        int height;

        WindowMode(String name, UserAgent userAgent, int width, int height) {
            this.name = name;
            this.userAgent = userAgent;
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
        init();
    }

    private void init() {
        removeAll();

        int width = this.windowMode.getWidth();
        int height = this.windowMode.getHeight();

        String url = HOME_PAGE;
        if (lastUrl != null) {
            url = lastUrl;
        }

        if (this.browserService != null) {
            this.browserService.close();
        }
        this.browserService = new JcefBrowserService(url);
        this.browserService.setUserAgent(windowMode.getUserAgent());

        Component browserUI = browserService.getUI();
        Dimension browserDimension = new Dimension(width, height);
        if (windowMode == WindowMode.SMALL) {
            browserUI.setMinimumSize(null);
            browserUI.setPreferredSize(browserDimension);
        } else {
            browserUI.setPreferredSize(null);
            browserUI.setMinimumSize(browserDimension);
        }

        JTextField urlField = new JTextField(url);
        urlField.setPreferredSize(new Dimension(width * 2 / 3, 30));

        Dimension buttonDimension = new Dimension(50, 25);
        Box hbox = Box.createHorizontalBox();

        ComboBox windowModeBox = new ComboBox();
        windowModeBox.setPreferredSize(buttonDimension);
        for (WindowMode mode : WindowMode.values()) {
            windowModeBox.addItem(mode.getName());
        }
        windowModeBox.setSelectedItem(windowMode.getName());
        windowModeBox.addItemListener(l -> {
            windowMode = WindowMode.getMode(l.getItem().toString());
            init();
        });
        hbox.add(windowModeBox);

        JButton exitButton = new JButton("✕");
        exitButton.setToolTipText("退出");
        exitButton.setPreferredSize(buttonDimension);
        exitButton.addActionListener(l -> Command.OVER.exec());
        hbox.add(exitButton);

        JButton homeButton = new JButton("♨");
        homeButton.setToolTipText("主页");
        homeButton.setPreferredSize(buttonDimension);
        homeButton.addActionListener(l -> browserService.loadURL(HOME_PAGE));
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

        setLayout(new BorderLayout());
        add(urlPanel, BorderLayout.NORTH);
        add(browserUI, BorderLayout.CENTER);
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
                SwingUtilities.invokeLater(() -> init());
            }
        });
    }

    public void close() {
        this.browserService.close();
    }

}
