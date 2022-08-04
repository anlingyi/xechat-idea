package cn.xeblog.plugin.action;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.plugin.entity.TextRender;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.enums.Style;
import cn.xeblog.plugin.mode.ModeContext;
import com.intellij.ide.BrowserUtil;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class ConsoleAction {

    private static JTextPane console;

    private static JPanel panel;

    private static JScrollPane consoleScroll;

    private static boolean isNewLine;

    public static void updateUI() {
        SwingUtilities.invokeLater(() -> console.updateUI());
    }

    public static void renderText(List<TextRender> list) {
        for (TextRender textRender : list) {
            renderText(textRender.getText(), textRender.getStyle());
        }
    }

    public static void renderText(String text) {
        renderText(text, Style.DEFAULT);
    }

    public static void renderText(String text, Style style) {
        if (isNewLine) {
            ModeContext.getMode().renderTextBefore(text);
        }

        render(text, style.get());

        isNewLine = text.endsWith("\n");
    }

    public static void showSimpleMsg(String msg) {
        renderText(msg + "\n", Style.DEFAULT);
    }

    public static void render(String content, AttributeSet attributeSet) {
        Document document = console.getDocument();
        try {
            document.insertString(document.getLength(), content, attributeSet);
            if (document.getLength() > 10000) {
                document.remove(0, 2000);
                document.insertString(0, "...", Style.DEFAULT.get());
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        gotoConsoleLow();
    }

    public static void renderImageLabel(JLabel label) {
        atomicExec(() -> {
            renderText("[");
            renderComponent(label);
            renderText("]\n");
        });
    }

    public static void renderUrl(String title, String url) {
        JLabel label = new JLabel(title);
        label.setAlignmentY(0.85f);
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        label.setForeground(StyleConstants.getForeground(Style.DEFAULT.get()));
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                BrowserUtil.browse(url);
            }
        });
        renderComponent(label);
    }

    public static void renderComponent(Component component) {
        JScrollBar verticalScrollBar = consoleScroll.getVerticalScrollBar();
        int beforeScrollVal = verticalScrollBar.getValue();
        updateCaretPosition(-1);
        console.insertComponent(component);
        SwingUtilities.invokeLater(() -> verticalScrollBar.setValue(beforeScrollVal));
    }

    public static void clean() {
        console.setText("");
    }

    public static void setConsoleTitle(String title) {
        synchronized (panel) {
            ((TitledBorder) panel.getBorder()).setTitle(title);
            panel.updateUI();
        }
    }

    public static void gotoConsoleLow() {
        JScrollBar verticalScrollBar = consoleScroll.getVerticalScrollBar();
        if (verticalScrollBar.getValue() + 20 < verticalScrollBar.getMaximum() - verticalScrollBar.getHeight()) {
            return;
        }

        updateCaretPosition(-1);
    }

    public static void showErrorMsg() {
        ConsoleAction.showSimpleMsg("输入的命令有误！帮助命令：" + Command.HELP.getCommand());
    }

    public static void showLoginMsg() {
        ConsoleAction.showSimpleMsg("请先登录！登录命令：" + Command.LOGIN.getCommand() + "，帮助命令：" + Command.HELP.getCommand());
    }

    private static void bindPopupMenu() {
        JPopupMenu jPopupMenu = new JPopupMenu("右键菜单");
        console.setComponentPopupMenu(jPopupMenu);

        JMenuItem copyItem = new JMenuItem("复制内容");
        copyItem.addActionListener(ev -> {
            String selectedText = console.getSelectedText();
            if (StrUtil.isBlank(selectedText)) {
                return;
            }

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            StringSelection contents = new StringSelection(selectedText);
            clipboard.setContents(contents, null);
        });

        JMenuItem searchItem = new JMenuItem("百度搜索");
        searchItem.addActionListener(ev -> {
            String selectedText = console.getSelectedText();
            if (StrUtil.isBlank(selectedText)) {
                return;
            }

            BrowserUtil.browse("https://www.baidu.com/s?wd=" + selectedText);
        });

        JMenuItem openUrlItem = new JMenuItem("打开网址");
        openUrlItem.addActionListener(ev -> {
            String selectedText = console.getSelectedText();
            if (StrUtil.isBlank(selectedText)) {
                return;
            }

            if (!selectedText.startsWith("http")) {
                selectedText = "https://" + selectedText;
            }

            BrowserUtil.browse(selectedText);
        });

        jPopupMenu.add(copyItem);
        jPopupMenu.add(searchItem);
        jPopupMenu.add(openUrlItem);
        jPopupMenu.addSeparator();

        Map<String, Command> commandMap = new LinkedHashMap<>();
        commandMap.put("快速登录", Command.LOGIN);
        commandMap.put("退！退！退！", Command.LOGOUT);
        commandMap.put("清屏", Command.CLEAN);
        commandMap.put("帮助", Command.HELP);

        commandMap.forEach((k, v) -> jPopupMenu.add(k).addActionListener(l -> {
            ConsoleAction.showSimpleMsg(v.getCommand());
            v.exec();
        }));
    }

    public static void setConsole(JTextPane console) {
        ConsoleAction.console = console;
        console.setEditorKit(new WarpEditorKit());
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(simpleAttributeSet, 0.2f);
        console.setParagraphAttributes(simpleAttributeSet, false);

        bindPopupMenu();
    }

    public static void setPanel(JPanel panel) {
        ConsoleAction.panel = panel;
    }

    public static void setConsoleScroll(JScrollPane consoleScroll) {
        ConsoleAction.consoleScroll = consoleScroll;
    }

    public static void showSystemMsg(String time, String msg) {
        ConsoleAction.renderText(String.format("[%s] 系统消息：%s\n", time, msg), Style.SYSTEM_MSG);
    }

    private static void updateCaretPosition(int position) {
        int copyPosition = position;
        atomicExec(() -> {
            int pos = copyPosition;
            if (pos == -1) {
                pos = console.getDocument().getLength();
            }
            console.setCaretPosition(pos);
        });
    }

    public static class WarpEditorKit extends StyledEditorKit {

        private ViewFactory defaultFactory = new WarpColumnFactory();

        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }

        private class WarpColumnFactory implements ViewFactory {

            public View create(Element elem) {
                String kind = elem.getName();
                if (kind != null) {
                    switch (kind) {
                        case AbstractDocument.ContentElementName:
                            return new WarpLabelView(elem);
                        case AbstractDocument.ParagraphElementName:
                            return new ParagraphView(elem);
                        case AbstractDocument.SectionElementName:
                            return new BoxView(elem, View.Y_AXIS);
                        case StyleConstants.ComponentElementName:
                            return new ComponentView(elem);
                        case StyleConstants.IconElementName:
                            return new IconView(elem);
                    }
                }

                return new LabelView(elem);
            }
        }

        private class WarpLabelView extends LabelView {

            public WarpLabelView(Element elem) {
                super(elem);
            }

            @Override
            public float getMinimumSpan(int axis) {
                switch (axis) {
                    case View.X_AXIS:
                        return 0;
                    case View.Y_AXIS:
                        return super.getMinimumSpan(axis);
                    default:
                        throw new IllegalArgumentException("Invalid axis: " + axis);
                }
            }
        }

    }

    public static void atomicExec(Runnable runnable) {
        synchronized (console) {
            runnable.run();
        }
    }

}
