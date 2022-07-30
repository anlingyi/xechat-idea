package cn.xeblog.plugin.ui.component;

import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.entity.Emoji;
import cn.xeblog.plugin.enums.Style;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linweiyuan
 * @date 2022/07/30
 */
public class EmojiWindow extends JWindow {
    private static final int WINDOW_WIDTH = 500;
    private static final int WINDOW_HEIGHT = 300;

    private static final String EMOJI_URL = "https://emojixd.com";
    private static final int EMOJI_PANEL_COLUMNS = 10;
    private static final int EMOJI_BORDER_COLOR = 0xE1E2E3;

    private final JButton emojiButton;

    public EmojiWindow(JButton emojiButton, JTextArea contentArea) {
        this.emojiButton = emojiButton;

        setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));

        try {
            // Emoji 标签页
            JTabbedPane tabbedPane = new JBTabbedPane();
            Elements elements = Jsoup.connect(EMOJI_URL).get().select(".group-header.center.p1");
            for (Element element : elements) {
                JPanel emojiPanel = new JPanel();
                List<Emoji> emojiList = getEmojiList(element);
                emojiPanel.setLayout(new GridLayout(emojiList.size() / EMOJI_PANEL_COLUMNS + 1, EMOJI_PANEL_COLUMNS));
                setupEmojiPanel(emojiList, contentArea, emojiPanel);
                tabbedPane.addTab(element.select(".h3").text(), new JBScrollPane(emojiPanel));
            }
            setContentPane(tabbedPane);
            setAlwaysOnTop(true);
        } catch (Exception e) {
            ConsoleAction.renderText(String.format("Emoji 初始化失败: [%s]\n", e), Style.WARN);
        }
    }

    private List<Emoji> getEmojiList(Element element) throws IOException {
        List<Emoji> emojiList = new ArrayList<>();
        Jsoup.connect(EMOJI_URL + element.attr("href"))
                .get()
                .select(".col.md-col-3.col-6")
                .forEach(emojiItem -> {
                    String text = emojiItem.select(".btn-copy").attr("data-clipboard-text");
                    String description = emojiItem.select(".truncate").attr("title");

                    Emoji emoji = new Emoji();
                    emoji.setText(text);
                    emoji.setDescription(description);
                    emojiList.add(emoji);
                });
        return emojiList;
    }

    private void setupEmojiPanel(List<Emoji> emojiList, JTextArea contentArea, JPanel emojiPanel) {
        Border emojiDefaultBorder = BorderFactory.createLineBorder(new JBColor(EMOJI_BORDER_COLOR, 0));
        Border emojiHoverBorder = BorderFactory.createLineBorder(JBColor.BLUE);

        emojiList.forEach(emoji -> {
            JBLabel emojiLabel = new JBLabel(emoji.getText());
            emojiLabel.setBorder(emojiDefaultBorder);
            emojiLabel.setToolTipText(emoji.getDescription());
            emojiLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        JBLabel emoji = (JBLabel) (e.getSource());
                        contentArea.append(emoji.getText());
                        contentArea.requestFocus();
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JBLabel) e.getSource()).setBorder(emojiHoverBorder);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    ((JBLabel) e.getSource()).setBorder(emojiDefaultBorder);
                }
            });
            emojiPanel.add(emojiLabel);
        });
    }

    @Override
    public void setVisible(boolean show) {
        if (show) {
            // Emoji 面板展开位置
            Dimension preferredSize = getPreferredSize();
            Point point = emojiButton.getLocationOnScreen();
            setBounds(point.x - preferredSize.width + emojiButton.getWidth(), point.y - preferredSize.height, preferredSize.width, preferredSize.height);
        }
        super.setVisible(show);
    }
} 