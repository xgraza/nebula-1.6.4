package us.nebula.client.impl.command;

import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.parser.CommandContext;

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
