package wtf.nebula.client.api.registry;

import wtf.nebula.client.utils.client.Wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BaseRegistry<T> implements Wrapper {
    protected final Map<Class<?>, T> registryMap = new ConcurrentHashMap<>();
    protected final List<T> registry = new ArrayList<>();

    protected void add(T instance) {
        registryMap.put(instance.getClass(), instance);
        registry.add(instance);
    }
}
