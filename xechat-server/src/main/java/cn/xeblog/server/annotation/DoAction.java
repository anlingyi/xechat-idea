package cn.xeblog.server.annotation;


import cn.xeblog.commons.enums.Action;

import java.lang.annotation.*;

/**
 * @author anlingyi
 * @date 2021/9/19 3:43 下午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoAction {

    Action value();

}
