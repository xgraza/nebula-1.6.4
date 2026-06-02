package ez.nebula.client.api.manager.key.trait;

/**
 * @author xgraza
 * @since 02/14/25
 */
@FunctionalInterface
public interface KeyAction
{
    void inhibit(final boolean state);
}
