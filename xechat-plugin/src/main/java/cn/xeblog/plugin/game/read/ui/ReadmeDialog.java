package cn.xeblog.plugin.game.read.ui;

import cn.hutool.core.io.IoUtil;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * @author LYF
 * @date 2022-07-22
 */
public class ReadmeDialog extends JDialog {

    private JPanel content;

    public ReadmeDialog() {
        initUI();
    }

    private void initUI() {
        this.pack();
        this.setSize(300, 300);
        this.setLocationRelativeTo(null);

        content = new JPanel(new BorderLayout());
        content.setBounds(0, 0, 300, 300);

        InputStream inputStream = ReadmeDialog.class.getResourceAsStream("/readme/read-readme.html");
        String readme = IoUtil.read(inputStream, StandardCharsets.UTF_8);
        JEditorPane readmePane = new JEditorPane();
        readmePane.setContentType("text/html");
        readmePane.setText(readme);
        content.add(new JScrollPane(readmePane));

        this.setContentPane(content);
        this.setModal(true);
        this.setTitle("阅读使用说明");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.setVisible(true);
    }
}
