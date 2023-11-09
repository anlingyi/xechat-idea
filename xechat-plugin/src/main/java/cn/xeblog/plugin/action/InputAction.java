package cn.xeblog.plugin.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.Action;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Command;
import cn.xeblog.plugin.listener.MainWindowInitializedEventListener;
import cn.xeblog.plugin.tools.encourage.cache.EncourageCache;
import cn.xeblog.plugin.ui.MainWindow;
import cn.xeblog.plugin.util.CommandHistoryUtils;
import cn.xeblog.plugin.util.UploadUtils;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author anlingyi
 * @date 2022/8/4 9:48 PM
 */
public class InputAction implements MainWindowInitializedEventListener {

    private static JTextArea contentArea;

    private static JPanel leftTopPanel;

    private static JBList jbList;

    private static boolean isProactive;

    /**
     * 冻结时间
     */
    private final static long FREEZE_TIME = 15 * 1000;
    /**
     * 间隔时间
     */
    private final static long INTERVAL_TIME = 10 * 1000;

    /**
     * 冻结结束时间
     */
    private static long freezeEndTime;

    /**
     * 消息发送计数
     */
    private static int sendCounter = -1;

    /**
     * 消息发送计数开始时间
     */
    private static long sendCounterStartTime;

    @Override
    public void afterInit(MainWindow mainWindow) {
        contentArea = mainWindow.getContentArea();
        leftTopPanel = mainWindow.getLeftTopPanel();

        bindKeyListener();
    }

    private static void bindKeyListener() {
        contentArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                String content = contentArea.getText();

                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    // 阻止默认事件
                    e.consume();
                    sendMsg();
                }

                if (e.getKeyCode() == KeyEvent.VK_TAB && leftTopPanel.isVisible()) {
                    e.consume();
                }

                if (e.getKeyCode() == 38 || e.getKeyCode() == 40) {
                    e.consume();
                    if (isProactive && leftTopPanel.isVisible() && jbList != null) {
                        jbList.requestFocus();
                    } else if (StrUtil.isBlank(content) || content.startsWith(Command.COMMAND_PREFIX)) {
                        String cmd = null;
                        if (e.getKeyCode() == 38) {
                            cmd = CommandHistoryUtils.getPrevCommand();
                        } else if (e.getKeyCode() == 40) {
                            cmd = CommandHistoryUtils.getNextCommand();
                        }

                        if (StrUtil.isNotBlank(cmd)) {
                            isProactive = false;
                            contentArea.setText(cmd);
                        }
                    }
                } else {
                    isProactive = true;
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
                    pasteImage();
                } else {
                    // @用户和命令提示
                    atUserAndCommandTips(e);
                }
            }
        });
    }

    private static void pasteImage() {
        // 粘贴图片
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable transferable = clipboard.getContents(null);
        try {
            if (transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                List<File> fileList = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                UploadUtils.uploadImageFile(fileList.get(0));
                clean();
            } else if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                Image image = (Image) transferable.getTransferData(DataFlavor.imageFlavor);
                UploadUtils.uploadImage(image);
                clean();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private static void atUserAndCommandTips(KeyEvent e) {
        boolean isAt = false;
        List<String> dataList = null;
        String content = contentArea.getText();
        int caretPosition = contentArea.getCaretPosition();
        int atIndex = -1;
        String commandPrefix = Command.COMMAND_PREFIX;
        if (content.startsWith(commandPrefix)) {
            Map<String, String> commandMap = new LinkedHashMap<>();
            for (Command command : Command.values()) {
                commandMap.put(command.getCommand(), command.getCommand() + " (" + command.getDesc() + ")");
            }

            String command = content.substring(1);
            if (StrUtil.isBlank(command)) {
                dataList = new ArrayList<>(commandMap.values());
            } else {
                final List<String> matchList = new ArrayList<>();
                commandMap.forEach((k, v) -> {
                    if (k.toLowerCase().contains(command.toLowerCase()) || command.startsWith(k)) {
                        matchList.add(v);
                    }
                });
                dataList = matchList;
            }
        } else {
            if (DataCache.isOnline) {
                isAt = true;
                String atContent = content.substring(0, caretPosition);
                atIndex = atContent.lastIndexOf("@");
                if (atIndex > -1) {
                    List<User> onlineUserList = new ArrayList<>(DataCache.userMap.values());
                    onlineUserList.sort((u1, u2) -> {
                        int o1 = u1.getRole().ordinal();
                        int o2 = u2.getRole().ordinal();
                        if (o1 < o2) {
                            return -1;
                        }
                        if (o1 == o2) {
                            return 0;
                        }
                        return 1;
                    });

                    List<String> allUserList = new ArrayList<>();
                    onlineUserList.forEach(user -> allUserList.add(user.getUsername()));

                    String name = content.substring(atIndex + 1, caretPosition);
                    if (StrUtil.isNotBlank(name)) {
                        dataList = new ArrayList<>();
                        for (String user : allUserList) {
                            if (user.toLowerCase().contains(name.toLowerCase())) {
                                dataList.add(user);
                            }
                        }
                    }

                    if (atIndex + 1 == caretPosition && CollUtil.isEmpty(dataList)) {
                        dataList = allUserList;
                    }
                }
            }
        }

        leftTopPanel.setVisible(false);
        leftTopPanel.removeAll();

        if (CollectionUtil.isNotEmpty(dataList)) {
            boolean copyIsAt = isAt;
            int copyAtIndex = atIndex;

            Runnable runnable = () -> {
                if (jbList == null) {
                    return;
                }

                Object selectedValue = jbList.getSelectedValue();
                if (selectedValue == null) {
                    return;
                }

                String value = selectedValue.toString();
                if (copyIsAt) {
                    contentArea.replaceRange(value + " ", copyAtIndex + 1, caretPosition);
                } else {
                    contentArea.setText(value.substring(0, value.indexOf(" ")));
                }

                requestFocus();
                leftTopPanel.setVisible(false);
                leftTopPanel.removeAll();
            };

            jbList = new JBList();
            jbList.setListData(dataList.toArray());
            jbList.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                        runnable.run();
                    }
                }
            });

            jbList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        runnable.run();
                    }
                }
            });

            JBScrollPane scrollPane = new JBScrollPane(jbList);
            scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

            leftTopPanel.setMinimumSize(new Dimension(0, 100));
            leftTopPanel.add(scrollPane);
            leftTopPanel.setVisible(true);

            if (e.getKeyCode() == KeyEvent.VK_TAB) {
                String value = dataList.get(0);
                if (copyIsAt) {
                    contentArea.replaceRange(value + " ", copyAtIndex + 1, caretPosition);
                } else {
                    contentArea.replaceRange(value.substring(0, value.indexOf(" ")), 0, caretPosition);
                }
            }
        }

        leftTopPanel.updateUI();
    }

    private static void sendMsg() {
        String content = contentArea.getText();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        if (content.length() > 200) {
            ConsoleAction.showSimpleMsg("发送的内容长度不能超过200字符！");
        } else {
            if (content.startsWith(Command.COMMAND_PREFIX)) {
                ConsoleAction.showSimpleMsg(content);
                Command.handle(content);
            } else {
                if (DataCache.isOnline) {

                    if (checkFreeze()) {
                        return;
                    }

                    UserMsgDTO.MsgType msgType = UserMsgDTO.MsgType.TEXT;
                    String[] toUsers;
                    if (EncourageCache.supportPrivateChat && EncourageCache.privateChatUser != null) {
                        toUsers = new String[]{EncourageCache.privateChatUser.getUsername()};
                        msgType = UserMsgDTO.MsgType.PRIVATE;
                    } else if (!EncourageCache.atUsers.isEmpty()) {
                        // 艾特勾选的用户
                        toUsers = getToUsers(content, EncourageCache.atUsers.stream().map(User::getUsername).collect(Collectors.toList()));
                        content += "       @批量艾特[" + EncourageCache.atUsers.size() + "]人";
                    } else {
                        toUsers = getToUsers(content, Collections.emptyList());
                    }

                    MessageAction.send(new UserMsgDTO(content, msgType, toUsers), Action.CHAT);
                } else {
                    ConsoleAction.showLoginMsg();
                }
            }
            clean();
        }

        ConsoleAction.gotoConsoleLow();
    }

    private static boolean checkFreeze() {
        if (sendCounter == 0 && System.currentTimeMillis() - sendCounterStartTime < INTERVAL_TIME) {
            sendCounterStartTime = 0;
            freezeEndTime = System.currentTimeMillis() + FREEZE_TIME;
        }

        long endTime = freezeEndTime - System.currentTimeMillis();
        if (endTime > 0) {
            ConsoleAction.showSimpleMsg("消息发送过于频繁，请于" + endTime / 1000 + "s后再发...");
            return true;
        }

        if (sendCounter == -1) {
            sendCounter = 0;
        }
        if (++sendCounter >= 6) {
            sendCounter = 0;
        }
        if (sendCounter == 1) {
            sendCounterStartTime = System.currentTimeMillis();
        }
        return false;
    }

    public static String[] getToUsers(String content, List<String> extraList) {
        String[] toUsers = null;
        List<String> toUserList = new ArrayList<>(ReUtil.findAll("(@)([^\\s]+)([\\s]*)", content, 2));
        toUserList.addAll(extraList);
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
                toUsers = ArrayUtil.toArray(new HashSet<>(toUserList), String.class);
            }
        }
        return toUsers;
    }

    public static void clean() {
        contentArea.setText("");
    }

    public static boolean requestFocus() {
        contentArea.requestFocusInWindow();
        return contentArea.isFocusOwner();
    }

    public static boolean restCursor() {
        int len = StrUtil.length(contentArea.getText());
        contentArea.setCaretPosition(len);
        return requestFocus();
    }

}
