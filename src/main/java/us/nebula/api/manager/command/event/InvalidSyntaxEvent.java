package us.nebula.api.manager.command.event;

/**
 * @author xgraza
 * @since 02/16/25
 */
@FunctionalInterface
public interface InvalidSyntaxEvent
{
    void execute(final String syntax);
}
