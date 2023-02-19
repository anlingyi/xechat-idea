package cn.xeblog.commons.entity.react.result;

import lombok.Data;

/**
 * @author anlingyi
 * @date 2023/2/18 12:16 AM
 */
@Data
public class DownloadReactResult {

    private String fileName;

    private byte[] bytes;

}
