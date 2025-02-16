package us.nebula.api.manager.command.event;

import us.nebula.api.manager.command.Command;

/**
 * @author xgraza
 * @since 02/16/25
 */
@FunctionalInterface
public interface CommandDispatchEvent
{
    void execute(final Command command, final int result);
}
