package us.nebula.impl.command;

import us.nebula.api.DebugFeature;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;

/**
 * @author xgraza
 * @since 03/24/25
 */
@DebugFeature
@CommandManifest(aliases = {"gc"},
        description = "Runs the garbage collector")
public final class GCCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder.dispatchSingle(() ->
        {
            System.gc();
            return CommandResult.SUCCESS_DEFAULT;
        });
    }
}
