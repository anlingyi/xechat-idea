package cn.xeblog.plugin.ui;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.plugin.action.MessageAction;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
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
import java.util.ArrayList;
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

    private static long lastSendTime;

    private MainWindow() {
        init();
    }

    private void init() {
        ConsoleAction.setConsole(console);
        ConsoleAction.setPanel(leftPanel);
        ConsoleAction.setConsoleScroll(consoleScroll);

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
                            cleanContent();
                        } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                            Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                            UploadUtils.uploadImage(image);
                            cleanContent();
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

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getRightPanel() {
        return rightPanel;
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
                    long sendTime = System.currentTimeMillis();
                    if (lastSendTime + 800 > sendTime) {
                        ConsoleAction.showSimpleMsg("休息一下哦~");
                        return;
                    }

                    lastSendTime = sendTime;
                    String[] toUsers = null;
                    List<String> toUserList = ReUtil.findAll("(@)([^\\s]+)([\\s]*)", content, 2);
                    if (CollectionUtil.isNotEmpty(toUserList)) {
                        List<String> removeList = new ArrayList<>();
                        for (String toUser : toUserList) {
                            if (DataCache.getUser(toUser) == null) {
                                removeList.add(toUser);
                            }
                        }
                        if (!removeList.isEmpty()) {
                            toUserList.removeAll(removeList);
                        }
                        if (!toUserList.isEmpty()) {
                            toUserList.add(DataCache.username);
                            toUsers = ArrayUtil.toArray(toUserList, String.class);
                        }
                    }
                    MessageAction.send(new UserMsgDTO(content, toUsers), Action.CHAT);
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
