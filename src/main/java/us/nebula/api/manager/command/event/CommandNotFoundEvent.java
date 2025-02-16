package us.nebula.api.manager.command.event;

/**
 * @author xgraza
 * @since 02/16/25
 */
@FunctionalInterface
public interface CommandNotFoundEvent
{
    void execute(final String commandName);
}
