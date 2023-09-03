package cn.xeblog.server.action.handler.react;

import cn.xeblog.commons.entity.User;
import cn.xeblog.commons.entity.react.React;
import cn.xeblog.commons.entity.react.request.DownloadReact;
import cn.xeblog.commons.entity.react.result.DownloadReactResult;
import cn.xeblog.commons.entity.react.result.ReactResult;
import cn.xeblog.server.annotation.DoReact;
import cn.xeblog.server.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2023/2/18 12:17 AM
 */
@Slf4j
@DoReact(React.DOWNLOAD)
public class DownloadReactHandler extends AbstractReactHandler<DownloadReact, DownloadReactResult> {

    @Override
    protected void process(User user, DownloadReact body, ReactResult<DownloadReactResult> result) {
        try {
            byte[] bytes = FileUtil.getFile(body.getFileName());
            if (bytes == null) {
                result.setMsg("文件不存在!");
                return;
            }

            DownloadReactResult data = new DownloadReactResult();
            data.setFileName(body.getFileName());
            data.setBytes(bytes);
            result.setData(data);
            result.setSucceed(true);
        } catch (Exception e) {
            log.error("文件下载出现异常", e);
            result.setMsg("文件下载失败！");
        }
    }

}
