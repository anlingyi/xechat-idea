package cn.xeblog.server.util;

import cn.hutool.core.util.StrUtil;
import cn.xeblog.server.config.GlobalConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anlingyi
 * @date 2023/9/3 5:49 PM
 */
@Slf4j
public class FileUtil {

    /**
     * 获取文件
     *
     * @param fileName
     * @return
     */
    public static byte[] getFile(String fileName) {
        if (StrUtil.isNotBlank(fileName)) {
            String filePath = GlobalConfig.UPLOAD_FILE_PATH + "/" + fileName;
            try {
                return cn.hutool.core.io.FileUtil.readBytes(filePath);
            } catch (Exception e) {
                log.info("文件获取失败, filePath: {}", filePath, e);
            }
        }

        return null;
    }

}
