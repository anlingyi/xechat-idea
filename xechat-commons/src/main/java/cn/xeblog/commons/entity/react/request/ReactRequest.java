package cn.xeblog.commons.entity.react.request;

import cn.xeblog.commons.entity.react.BaseReact;
import cn.xeblog.commons.entity.react.React;
import lombok.Data;

/**
 * @author anlingyi
 * @date 2022/9/19 8:12 AM
 */
@Data
public class ReactRequest<T> extends BaseReact {

    private T body;

    private React react;

}
