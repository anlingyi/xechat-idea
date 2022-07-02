package cn.xeblog.plugin.action.handler.message;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.cache.DataCache;
import cn.xeblog.plugin.enums.Style;
import cn.xeblog.plugin.util.NotifyUtils;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.application.ApplicationManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.USER)
public class UserMessageHandler extends AbstractMessageHandler<UserMsgDTO> {

    private static final String IMAGES_DIR = System.getProperty("user.home") + "/xechat/images";

    private static final Map<String, ImageFileLabel> IMAGE_File_LABEL_MAP = new ConcurrentHashMap<>();

    @Data
    @NoArgsConstructor
    private class ImageFileLabel {

        private JLabel label;

        private ByteBuf byteBuf;

        private int len;

        private int writeLen;

        public ImageFileLabel(JLabel label, int len) {
            this.label = label;
            this.len = len;
            this.byteBuf = Unpooled.buffer(len);
        }

        public byte[] write(int index, byte[] bytes) {
            synchronized (this.byteBuf) {
                this.byteBuf.setBytes(index, bytes);
                writeLen += bytes.length;
                if (writeLen == len) {
                    byte[] allBytes = read();
                    byteBuf.release();
                    return allBytes;
                }
                return null;
            }
        }

        public byte[] read() {
            return this.byteBuf.array();
        }

    }

    @Override
    protected void process(Response<UserMsgDTO> response) {
        User user = response.getUser();
        UserMsgDTO body = response.getBody();
        boolean isImage = body.getMsgType() == UserMsgDTO.MsgType.IMAGE;
        if (isImage) {
            byte[] bytes = (byte[]) body.getContent();
            ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
            int fileNameLength = byteBuf.readInt();
            String fileName = new String(ByteBufUtil.getBytes(byteBuf.readBytes(fileNameLength)));
            int fileLength = byteBuf.readInt();
            int index = byteBuf.readInt();
            ImageFileLabel imageFileLabel = IMAGE_File_LABEL_MAP.get(fileName);
            if (imageFileLabel == null) {
                JLabel imgLabel = new JLabel("图片加载中...");
                imageFileLabel = new ImageFileLabel(imgLabel, fileLength);
                IMAGE_File_LABEL_MAP.put(fileName, imageFileLabel);
                imgLabel.setEnabled(false);
                imgLabel.setAlignmentY(0.85f);
                imgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                imgLabel.setForeground(StyleConstants.getForeground(Style.DEFAULT.get()));
                ConsoleAction.atomicExec(() -> {
                    ConsoleAction.renderText(String.format("[%s] %s(%s)：", response.getTime(), user.getUsername(),
                            user.getStatus().alias()), Style.USER_NAME);
                    ConsoleAction.renderImageLabel(imgLabel);
                });
            }

            byte[] writeBytes = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(writeBytes);
            byteBuf.release();
            byte[] fileBytes = imageFileLabel.write(index, writeBytes);
            if (fileBytes != null) {
                JLabel imgLabel = imageFileLabel.getLabel();
                GlobalThreadPool.execute(() -> {
                    String filePath = IMAGES_DIR + "/" + fileName;
                    File imageFile = new File(filePath);
                    if (!imageFile.exists()) {
                        FileUtil.mkdir(IMAGES_DIR);
                        try (FileOutputStream out = new FileOutputStream(imageFile)) {
                            out.write(fileBytes);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    imgLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            ApplicationManager.getApplication().invokeLater(() -> {
                                OpenFileAction.openFile(filePath, DataCache.project);
                            });
                        }
                    });
                    imgLabel.setEnabled(true);
                    imgLabel.setText("查看图片");
                    imgLabel.setToolTipText("点击查看图片");
                    ConsoleAction.updateUI();

                    IMAGE_File_LABEL_MAP.remove(fileName);
                });
            }
        } else {
            ConsoleAction.atomicExec(() -> {
                ConsoleAction.renderText(String.format("[%s] %s(%s)：", response.getTime(), user.getUsername(),
                        user.getStatus().alias()), Style.USER_NAME);
                boolean notified = body.hasUser(DataCache.username);
                Style style = Style.DEFAULT;
                String msg = (String) body.getContent();
                if (notified) {
                    style = Style.LIGHT;
                    if (!user.getUsername().equals(DataCache.username)) {
                        NotifyUtils.info(user.getUsername(), msg);
                    }
                }
                ConsoleAction.renderText(msg + "\n", style);
            });
        }
    }
}
