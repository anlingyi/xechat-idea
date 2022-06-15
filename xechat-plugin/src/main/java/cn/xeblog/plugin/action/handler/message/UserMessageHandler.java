package cn.xeblog.plugin.action.handler.message;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.enums.Style;
import com.intellij.ide.actions.OpenFileAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import javax.swing.*;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;

/**
 * @author anlingyi
 * @date 2020/8/19
 */
@DoMessage(MessageType.USER)
public class UserMessageHandler extends AbstractMessageHandler<UserMsgDTO> {

    private static final String IMAGES_DIR = System.getProperty("user.home") + "/xechat/images";

    @Override
    protected void process(Response<UserMsgDTO> response) {
        User user = response.getUser();
        UserMsgDTO body = response.getBody();
        boolean isImage = body.getMsgType() == UserMsgDTO.MsgType.IMAGE;
        ConsoleAction.renderText(String.format("[%s] %s(%s)：", response.getTime(), user.getUsername(),
                user.getStatus().alias()), Style.USER_NAME);
        if (isImage) {
            JLabel imgLabel = new JLabel("图片加载中...");
            imgLabel.setEnabled(false);
            imgLabel.setAlignmentY(0.85f);
            imgLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            imgLabel.setForeground(StyleConstants.getForeground(Style.DEFAULT.get()));
            ConsoleAction.renderImageLabel(imgLabel);

            GlobalThreadPool.execute(() -> {
                byte[] bytes = (byte[]) body.getContent();
                ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                int fileNameLength = byteBuf.readInt();
                String fileName = new String(ByteBufUtil.getBytes(byteBuf.readBytes(fileNameLength)));
                String filePath = IMAGES_DIR + "/" + fileName;
                File imageFile = new File(filePath);
                if (!imageFile.exists()) {
                    FileUtil.mkdir(IMAGES_DIR);
                    try (FileOutputStream out = new FileOutputStream(imageFile)) {
                        byteBuf.readBytes(out, byteBuf.readableBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                imgLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ApplicationManager.getApplication().invokeLater(() -> {
                            Project[] projects = ProjectManager.getInstance().getOpenProjects();
                            OpenFileAction.openFile(filePath, projects[projects.length - 1]);
                        });
                    }
                });
                imgLabel.setEnabled(true);
                imgLabel.setText("查看图片");
                imgLabel.setToolTipText("点击查看图片");
                imgLabel.updateUI();
            });
        } else {
            ConsoleAction.showSimpleMsg((String) body.getContent());
        }
    }

}
