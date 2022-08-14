package cn.xeblog.plugin.tools.browser.ui;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.enums.Command;
import com.intellij.ui.JBColor;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import org.cef.CefClient;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefDisplayHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;

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

    private String lastUrl;

    public BrowserUI() {
        initPanel();
    }

    private void initPanel() {
        int width = 300;
        int height = 400;
        String url = HOME_PAGE;
        if (lastUrl != null) {
            url = lastUrl;
        }

        JTextField urlField = new JTextField(url);
        urlField.setPreferredSize(new Dimension(200, 30));

        JBCefBrowser jbCefBrowser = new JBCefBrowser(url);
//        jbCefBrowser.setErrorPage(JBCefBrowserBase.ErrorPage.DEFAULT);
//        jbCefBrowser.setPageBackgroundColor(colorToHex(JBColor.background()));
        CefBrowser cefBrowser = jbCefBrowser.getCefBrowser();
        CefClient client = cefBrowser.getClient();

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {
            @Override
            public void onAddressChange(CefBrowser browser, CefFrame frame, String url) {
                if (!StrUtil.startWith(url, "http")) {
                    return;
                }

                lastUrl = url;
                urlField.setText(url);
            }
        });

        client.removeLifeSpanHandler();
        client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
                if (StrUtil.endWithAnyIgnoreCase(target_url, "jpg", "png", "gif", "svg", "pdf", "bmp", "webp")) {
                    return false;
                }

                cefBrowser.loadURL(target_url);
                return true;
            }

            @Override
            public void onBeforeClose(CefBrowser browser) {
                SwingUtilities.invokeLater(() -> {
                    jbCefBrowser.dispose();
                    removeAll();
                    initPanel();
                    updateUI();
                });
            }
        });

        Dimension buttonDimension = new Dimension(50, 25);
        Box hbox = Box.createHorizontalBox();
        JButton exitButton = new JButton("✕");
        exitButton.setToolTipText("退出");
        exitButton.setPreferredSize(buttonDimension);
        exitButton.addActionListener(l -> {
            jbCefBrowser.dispose();
            Command.OVER.exec();
        });
        hbox.add(exitButton);

        JButton homeButton = new JButton("♨");
        homeButton.setToolTipText("主页");
        homeButton.setPreferredSize(buttonDimension);
        homeButton.addActionListener(l -> cefBrowser.loadURL(HOME_PAGE));
        hbox.add(homeButton);

        JButton backButton = new JButton("←");
        backButton.setToolTipText("后退");
        backButton.setPreferredSize(buttonDimension);
        backButton.addActionListener(l -> cefBrowser.goBack());
        hbox.add(backButton);

        JButton forwardButton = new JButton("→");
        forwardButton.setToolTipText("前进");
        forwardButton.setPreferredSize(buttonDimension);
        forwardButton.addActionListener(l -> cefBrowser.goForward());
        hbox.add(forwardButton);

        JButton refreshButton = new JButton("⟳");
        refreshButton.setToolTipText("刷新");
        refreshButton.setPreferredSize(buttonDimension);
        refreshButton.addActionListener(l -> cefBrowser.reload());
        hbox.add(refreshButton);
        hbox.add(urlField);

        JPanel urlPanel = new JPanel();
        urlPanel.add(hbox);

        JPanel htmlPanel = new JPanel();
        htmlPanel.add(jbCefBrowser.getComponent());

        setMinimumSize(new Dimension(width, height));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());
        add(urlPanel, BorderLayout.NORTH);
        add(htmlPanel, BorderLayout.CENTER);

        urlField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String url = urlField.getText();
                    if (!StrUtil.startWithAny(url, "https://", "http://")) {
                        url = "https://" + url;
                    }
                    jbCefBrowser.loadURL(url);
                }
            }
        });
    }

    private String colorToHex(Color color) {
        return new StringBuffer()
                .append("#")
                .append(intToHex(color.getRed()))
                .append(intToHex(color.getGreen()))
                .append(intToHex(color.getBlue()))
                .toString();
    }

    private String intToHex(int value) {
        String hex = Integer.toHexString(value & 0xff);
        if (hex.length() == 1) {
            hex = "0" + hex;
        }
        return hex.toUpperCase();
    }

}
