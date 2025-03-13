package us.nebula.impl.command;

import us.nebula.ClientSettings;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CommandManifest(aliases = {"verbose"},
        description = "Sets verbose logging for the client")
public final class VerboseCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder.dispatchSingle(() ->
        {
            ClientSettings.VERBOSE_LOGGING = !ClientSettings.VERBOSE_LOGGING;
            ChatUtil.send("Set verbose logging to &d%s",
                    ClientSettings.VERBOSE_LOGGING);
            return CommandResult.SUCCESS;
        });
    }
}
