package cn.xeblog.plugin.factory;

import cn.xeblog.commons.factory.AbstractSingletonFactory;
import com.intellij.util.PathUtil;

/**
 * @author anlingyi
 * @date 2021/10/3 5:38 下午
 */
public abstract class AbstractIdeaSingletonFactory<K, V> extends AbstractSingletonFactory<K, V> {

    @Override
    protected Registry<K, V> setRegistry() {
        return new Registry(PathUtil.getJarPathForClass(AbstractIdeaSingletonFactory.class));
    }

}
