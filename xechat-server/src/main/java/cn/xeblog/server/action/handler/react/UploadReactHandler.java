package cn.xeblog.server.action.handler.react;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.IdUtil;
import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.UserMsgDTO;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.UploadReact;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.commons.entity.react.result.UploadReactResult;
import cn.xeblog.commons.enums.MessageType;
import cn.xeblog.server.action.ChannelAction;
import cn.xeblog.server.annotation.DoReact;
import cn.xeblog.server.builder.ResponseBuilder;
import cn.xeblog.server.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author anlingyi
 * @date 2022/9/19 8:53 AM
 */
@Slf4j
@DoReact(React.UPLOAD)
public class UploadReactHandler extends AbstractReactHandler<UploadReact, UploadReactResult> {

    @Override
    protected void process(User user, UploadReact body, ReactResult<UploadReactResult> result) {
        int maxSize = GlobalConfig.UPLOAD_FILE_MAX_SIZE;

        int len = ArrayUtil.length(body.getBytes());
        if (len > maxSize * 1024) {
            result.setSucceed(false);
            result.setMsg("发送的文件大小不能超过" + maxSize + "KB!");
        } else {
            String filePath = GlobalConfig.UPLOAD_FILE_PATH;
            String filename = IdUtil.fastUUID() + "." + body.getFileType();
            File imageFile = new File(filePath + "/" + filename);
            if (!imageFile.exists()) {
                FileUtil.mkdir(filePath);
                try (FileOutputStream out = new FileOutputStream(imageFile)) {
                    out.write(body.getBytes());

                    UploadReactResult data = new UploadReactResult();
                    data.setFileName(filename);
                    result.setData(data);

                    UserMsgDTO dto = new UserMsgDTO();
                    dto.setMsgType(UserMsgDTO.MsgType.IMAGE);
                    dto.setContent(filename);
                    ChannelAction.send(user, dto, MessageType.USER);
                } catch (Exception e) {
                    log.error("文件上传异常", e);
                    result.setSucceed(false);
                    result.setMsg("文件上传失败！");
                }
            }
        }

        user.send(ResponseBuilder.react(result));
    }

}
