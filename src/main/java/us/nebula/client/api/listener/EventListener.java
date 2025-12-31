package us.nebula.client.api.listener;

/**
 * @author xgraza
 * @since 02/14/25
 * @param <T>
 */
@FunctionalInterface
public interface EventListener<T extends Event>
{
    void execute(final T event);
}
