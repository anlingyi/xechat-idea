package cn.xeblog.commons.factory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author anlingyi
 * @date 2021/8/21 6:19 下午
 */
public abstract class AbstractSingletonFactory<K, V> {

    private Registry<K, V> registry;

    public AbstractSingletonFactory() {
        this.registry = new Registry();
        this.registration(registry);
    }

    public V produce(K key) {
        return this.registry.getInstance(key);
    }

    protected abstract void registration(Registry<K, V> registry);

    protected class Registry<K, V> {

        private final Map<K, V> instanceCaches = new HashMap<>();

        public void add(K key, Class<? extends V> clazz) {
            try {
                addInstance(key, clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void addInstance(K k, V v) {
            this.instanceCaches.put(k, v);
        }

        private V getInstance(K k) {
            return this.instanceCaches.get(k);
        }
    }

}
