package cn.xeblog.plugin.factory;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.xeblog.commons.util.ClassUtils;
import com.intellij.util.PathUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author anlingyi
 * @date 2022/8/5 7:06 上午
 */
public class ObjectFactory {

    private final Map<Object, Class> classMap;

    private final Class annotationClass;

    public ObjectFactory(Class annotationClass) {
        this.classMap = new HashMap<>();
        this.annotationClass = annotationClass;
        init();
    }

    private void init() {
        Set<Class<?>> clazzSet = ClassUtils.scan(PathUtil.getJarPathForClass(GameFactory.class), null, annotationClass);
        if (!clazzSet.isEmpty()) {
            clazzSet.forEach(clazz -> classMap.put(AnnotationUtil.getAnnotationValue(clazz, annotationClass), clazz));
        }
    }

    public Object produce(Object key) {
        Class clazz = classMap.get(key);
        if (clazz != null) {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
