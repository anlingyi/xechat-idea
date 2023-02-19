package cn.xeblog.commons.entity.react.result;

import cn.xeblog.commons.entity.react.BaseReact;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author anlingyi
 * @date 2022/9/19 8:21 AM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReactResult<T> extends BaseReact {

    private boolean succeed;

    private T data;

    private String msg;

}
