package cn.xeblog.plugin.tools.read.ui;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author LYF
 * @date 2022-07-22
 */
public class ReadmeDialog extends JDialog {

    public ReadmeDialog() {
        initUI();
    }

    private void initUI() {
        this.pack();
        this.setSize(500, 400);
        this.setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout());
        content.setBounds(0, 0, 500, 400);

        InputStream inputStream = ReadmeDialog.class.getResourceAsStream("/readme/read-readme.html");
        String readme = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        JEditorPane readmePane = new JEditorPane();
        readmePane.setContentType("text/html");
        readmePane.setText(readme);
        JBScrollPane scrollPane = new JBScrollPane(readmePane);
        content.add(scrollPane);

        SwingUtilities.invokeLater(() -> {
            ThreadUtil.sleep(500);
            JScrollBar scrollBar = scrollPane.getVerticalScrollBar();
            scrollBar.setValue(scrollBar.getMinimum());
        });

        this.setContentPane(content);
        this.setModal(true);
        this.setTitle("阅读使用说明");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
}
