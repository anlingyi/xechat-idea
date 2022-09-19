package cn.xeblog.commons.entity.react.result;

import cn.xeblog.commons.entity.react.BaseReact;
import lombok.Data;

/**
 * @author anlingyi
 * @date 2022/9/19 8:21 AM
 */
@Data
public class ReactResult<T> extends BaseReact {

    private T result;

}
