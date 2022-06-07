package cn.xeblog.plugin.annotation;

import cn.xeblog.commons.enums.Game;

import java.lang.annotation.*;

/**
 * @author anlingyi
 * @date 2022/6/6 12:33 下午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoGame {

    Game value();

}
