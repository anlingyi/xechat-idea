package cn.xeblog.plugin.action.handler.message;

import cn.hutool.core.io.FileUtil;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.plugin.action.ConsoleAction;
import cn.xeblog.commons.entity.Response;
import cn.xeblog.commons.entity.User;
import cn.xeblog.plugin.annotation.DoMessage;
import cn.xeblog.plugin.enums.Style;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

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
        ConsoleAction.renderText(String.format("[%s] %s(%s)：", response.getTime(), user.getUsername(),
                user.getStatus().alias()), Style.USER_NAME);
        UserMsgDTO body = response.getBody();
        if (body.getMsgType() == UserMsgDTO.MsgType.TEXT) {
            ConsoleAction.showSimpleMsg((String) body.getContent());
        } else {
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

            ConsoleAction.renderImage(filePath);
        }
    }

}
