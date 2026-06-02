package ez.nebula.client.api.listener;

/**
 * @param <T>
 * @author xgraza
 * @since 02/14/25
 */
@FunctionalInterface
public interface EventListener<T extends Event>
{
    void execute(final T event);
}
