package us.nebula.client.key;

/**
 * @author xgraza
 * @since 02/14/25
 */
@FunctionalInterface
public interface KeyAction
{
    void inhibit(final boolean state);
}
