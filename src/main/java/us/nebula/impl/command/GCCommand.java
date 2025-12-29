package us.nebula.impl.command;

import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.parser.CommandContext;

/**
 * @author xgraza
 * @since 08/12/25
 */
@CommandManifest(
        aliases = { "gc", "garbagecollect" },
        description = "Runs the garbage collector")
public final class GCCommand extends Command
{
    @Override
    public CommandResult dispatch(final CommandContext ctx)
    {
        System.gc();
        return ctx.ok("Ran the garbage collector");
    }
}
