package cn.xeblog.plugin.ui;

import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.game.AbstractGame;
import cn.xeblog.plugin.util.UploadUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

/**
 * @author anlingyi
 * @date 2020/5/26
 */
public class MainWindow {
    private JPanel mainPanel;
    private JTextPane console;
    private JTextArea contentArea;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JPanel contentPanel;
    private JScrollPane consoleScroll;

    private MainWindow() {
        init();
    }

    private void init() {
        ConsoleAction.setConsole(console);
        ConsoleAction.setPanel(leftPanel);
        ConsoleAction.setConsoleScroll(consoleScroll);
        AbstractGame.setMainPanel(rightPanel);

        Command.HELP.exec(null);

        contentArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    // 阻止默认事件
                    e.consume();
                    sendMsg();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((e.isControlDown() || e.isMetaDown()) && e.getKeyCode() == KeyEvent.VK_V) {
                    if (!DataCache.isOnline) {
                        ConsoleAction.showLoginMsg();
                        return;
                    }

                    // 粘贴图片
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    Transferable transferable = clipboard.getContents(null);
                    try {
                        if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                            List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                            UploadUtils.uploadImageFile(fileList.get(0));
                        } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                            Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                            UploadUtils.uploadImage(image);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        });
    }

    private static final MainWindow MAIN_WINDOW = new MainWindow();

    public static MainWindow getInstance() {
        return MAIN_WINDOW;
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    private void sendMsg() {
        String content = contentArea.getText();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        if (content.length() > 500) {
            ConsoleAction.showSimpleMsg("发送的内容长度不能超过500字符！");
        } else {
            if (content.startsWith(Command.COMMAND_PREFIX)) {
                ConsoleAction.showSimpleMsg(content);
                Command.handle(content);
            } else {
                if (DataCache.isOnline) {
                    MessageAction.send(content, Action.CHAT);
                } else {
                    ConsoleAction.showLoginMsg();
                }
            }
            cleanContent();
        }

        ConsoleAction.gotoConsoleLow();
    }

    private void cleanContent() {
        contentArea.setText("");
    }

}
