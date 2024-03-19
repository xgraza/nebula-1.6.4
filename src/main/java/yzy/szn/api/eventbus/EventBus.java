package yzy.szn.api.eventbus;

import io.netty.util.internal.ConcurrentSet;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * @author graza
 * @since 02/17/24
 */
@SuppressWarnings("unchecked")
public final class EventBus {

    private final LoggerInfoFactory logger;

    private final Map<Class<? extends Event>, List<Subscriber>> subscriptionMap = new ConcurrentHashMap<>();
    private final Set<Object> subscribers = new ConcurrentSet<>();

    public EventBus(final LoggerInfoFactory logger) {
        this.logger = logger;
    }

    public void subscribe(final Object subscriber) {
        if (subscribers.add(subscriber)) {

            // discovery
            final Field[] fields = subscriber.getClass().getDeclaredFields();
            for (final Field field : fields) {
                if (Consumer.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);

                    final Subscribe sub = field.getDeclaredAnnotation(Subscribe.class);

                    Consumer<?> value;
                    try {
                        value = (Consumer<?>) field.get(subscriber);
                    } catch (IllegalAccessException e) {
                        logger.log(e.getMessage());
                        continue;
                    }

                    final Class<? extends Event> eventClass = (Class<? extends Event>) ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0];

                    final List<Subscriber> subscriptions = subscriptionMap.computeIfAbsent(
                            eventClass, (unused) -> new CopyOnWriteArrayList<>());
                    subscriptions.add(new Subscriber(subscriber, (Consumer<Event>) value, sub));
                }
            }
        }
    }

    public void unsubscribe(final Object subscriber) {
        if (subscribers.remove(subscriber)) {
            for (final Class<?> eventClass : subscriptionMap.keySet()) {
                final List<Subscriber> subscriptions = subscriptionMap.get(eventClass);
                subscriptions.removeIf(sub -> sub.getParent().equals(subscriber));
            }
        }
    }

    public boolean dispatch(final Event event) {
        final List<Subscriber> subscriptions = subscriptionMap.get(event.getClass());
        if (subscriptions == null || subscriptions.isEmpty()) {
            return false;
        }

        for (final Subscriber subscriber : subscriptions) {
            if (!event.isCanceled() || subscriber.getEventLink().canceled()) {
                subscriber.invoke(event);
            }
        }

        return event.isCanceled();
    }

    @FunctionalInterface
    public interface LoggerInfoFactory {
        void log(final String message);
    }
}
