package cn.xeblog.plugin.annotation;

import cn.xeblog.plugin.tools.Tools;

import java.lang.annotation.*;

/**
 * @author anlingyi
 * @date 2022/8/5 5:14 上午
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DoTool {

    Tools value();

}
