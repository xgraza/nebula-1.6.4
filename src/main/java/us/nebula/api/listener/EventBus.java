package us.nebula.api.listener;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xgraza
 * @since 02/14/25
 */
@SuppressWarnings("unchecked")
public final class EventBus
{
    private static final Set<Object> subscribedListeners = new HashSet<>();
    private static final Map<Class<? extends Event>, Set<Subscriber>> eventSubscribers = new ConcurrentHashMap<>();

    public static boolean dispatch(final Event event)
    {
        final Set<Subscriber> subscribers = eventSubscribers.get(event.getClass());
        if (subscribers == null || subscribers.isEmpty())
        {
            return false;
        }
        boolean canceled = false;
        for (final Subscriber subscriber : subscribers)
        {
            if (!canceled || subscriber.getProperties().receiveCanceled())
            {
                subscriber.invoke(event);
                canceled = event.isCanceled();
            }
        }
        return canceled;
    }

    public static void subscribe(final Object object)
    {
        if (!subscribedListeners.add(object))
        {
            return;
        }
        for (final Field field : object.getClass().getDeclaredFields())
        {
            if (!EventListener.class.isAssignableFrom(field.getType())
                    || !field.isAnnotationPresent(Subscribe.class))
            {
                continue;
            }
            final Subscribe properties = field.getDeclaredAnnotation(Subscribe.class);
            field.setAccessible(true);
            EventListener<?> listener;
            try
            {
                listener = (EventListener<?>) field.get(object);
            } catch (final IllegalAccessException e)
            {
                throw new RuntimeException(e);
            }
            if (listener == null)
            {
                throw new RuntimeException(
                        "EventListener field in a registered subscriber must not be null");
            }
            final Class<?> eventClass = (Class<?>) ((ParameterizedType) field.getGenericType())
                    .getActualTypeArguments()[0];
            final Set<Subscriber> subscribers = eventSubscribers.computeIfAbsent(
                    (Class<? extends Event>) eventClass, (x) -> new LinkedHashSet<>());
            subscribers.add(new Subscriber(listener, properties, object));
        }
    }

    public static void unsubscribe(final Object object)
    {
        if (!subscribedListeners.remove(object))
        {
            return;
        }
        eventSubscribers.forEach(
                (eventClass, subscribers) -> subscribers.removeIf(
                        (subscriber) -> subscriber.getParent().equals(object)));
    }

    private EventBus()
    {
        throw new RuntimeException("fuck off");
    }
}
