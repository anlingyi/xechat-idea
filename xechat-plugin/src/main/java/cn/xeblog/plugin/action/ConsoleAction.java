package cn.xeblog.plugin.action;

import cn.xeblog.plugin.entity.TextRender;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.enums.Style;
import cn.xeblog.plugin.mode.ModeContext;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.util.List;

/**
 * @author anlingyi
 * @date 2020/6/1
 */
public class ConsoleAction {

    private static JTextPane console;

    private static JPanel panel;

    private static JScrollPane consoleScroll;

    private static boolean isNewLine;

    public static void renderText(List<TextRender> list) {
        for (TextRender textRender : list) {
            renderText(textRender.getText(), textRender.getStyle());
        }
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
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        gotoConsoleLow();
    }

    public static void clean() {
        console.setText("");
    }

    public synchronized static void setConsoleTitle(String title) {
        ((TitledBorder) panel.getBorder()).setTitle(title);
        panel.updateUI();
    }

    public synchronized static void gotoConsoleLow() {
        JScrollBar scrollBar = consoleScroll.getVerticalScrollBar();
        scrollBar.setValue(scrollBar.getMaximum());
        consoleScroll.updateUI();
    }

    public static void showErrorMsg() {
        ConsoleAction.showSimpleMsg("输入的命令有误！帮助命令：" + Command.HELP.getCommand());
    }

    public static void showLoginMsg() {
        ConsoleAction.showSimpleMsg("请先登录！登录命令：" + Command.LOGIN.getCommand());
    }

    public static void setConsole(JTextPane console) {
        ConsoleAction.console = console;
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
}
