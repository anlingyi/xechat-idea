package cn.xeblog.plugin.annotation;


import cn.xeblog.commons.enums.MessageType;

import java.lang.annotation.*;

/**
 * @author anlingyi
 * @date 2021/9/19 3:43 下午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoMessage {

    MessageType value();

}
