package cn.xeblog.commons.factory;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ClassUtil;
import cn.xeblog.commons.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 抽象单例工厂
 *
 * @author anlingyi
 * @date 2021/8/21 6:19 下午
 */
public abstract class AbstractSingletonFactory<K, V> {

    private final Registry<K, V> registry;

    public AbstractSingletonFactory() {
        this.registry = setRegistry();
        this.registration(registry);
    }

    protected Registry<K, V> setRegistry() {
        return new Registry(null);
    }

    public V produce(K key) {
        return this.registry.getInstance(key);
    }

    protected abstract void registration(Registry<K, V> registry);

    protected class Registry<K, V> {

        private final String jarPath;

        private final Map<K, V> instanceCaches = new HashMap<>();

        public Registry(String jarPath) {
            this.jarPath = jarPath;
        }

        public void add(K key, Class<? extends V> clazz) {
            try {
                addInstance(key, clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void addByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
            Set<Class<?>> classes;
            if (this.jarPath == null) {
                classes = ClassUtil.scanPackageByAnnotation(packageName, annotationClass);
            } else {
                classes = ClassUtils.scan(this.jarPath, packageName, annotationClass);
            }

            if (!CollectionUtil.isEmpty(classes)) {
                classes.forEach(clazz -> {
                    add(AnnotationUtil.getAnnotationValue(clazz, annotationClass), (Class<? extends V>) clazz);
                });
            }
        }

        public void addByAnnotation(Class<? extends Annotation> annotationClass) {
            addByAnnotation(null, annotationClass);
        }

        private void addInstance(K k, V v) {
            this.instanceCaches.put(k, v);
        }

        private V getInstance(K k) {
            return this.instanceCaches.get(k);
        }
    }

}
