package cn.xeblog.server.annotation;

import cn.xeblog.commons.entity.react.React;

import java.lang.annotation.*;

/**
 * @author anlingyi
 * @date 2022/9/19 8:40 AM
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoReact {

    React value();

}
