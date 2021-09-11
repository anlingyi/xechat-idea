package cn.xeblog.server.factory;

import cn.xeblog.commons.factory.AbstractSingletonFactory;
import cn.xeblog.server.service.AbstractResponseHistoryService;
import cn.xeblog.server.service.DefaultResponseHistoryService;

/**
 * @author anlingyi
 * @date 2021/9/11 6:38 下午
 */
public class ObjectFactory extends AbstractSingletonFactory<Class, Object> {

    private static final ObjectFactory INSTANCE = new ObjectFactory();

    private ObjectFactory() {
    }

    @Override
    protected void registration(Registry<Class, Object> registry) {
        registry.add(AbstractResponseHistoryService.class, DefaultResponseHistoryService.class);
    }

    public static <T> T getObject(Class<T> key) {
        return (T) INSTANCE.produce(key);
    }

}
