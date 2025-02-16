package us.nebula.api.manager.command.argument;

/**
 * @author xgraza
 * @since 4.0.0
 */
@FunctionalInterface
public interface ArgumentDispatcher<T>
{
    int dispatch(final Argument<T> argument);
}
