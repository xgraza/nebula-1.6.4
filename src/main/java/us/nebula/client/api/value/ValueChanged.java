package us.nebula.client.api.value;

/**
 * @author xgraza
 * @since 02/26/25
 */
@FunctionalInterface
public interface ValueChanged<T>
{
    void change(final T oldValue, final T value);
}
