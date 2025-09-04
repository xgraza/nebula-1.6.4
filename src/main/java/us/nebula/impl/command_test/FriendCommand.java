package us.nebula.impl.command_test;

import us.nebula.api.manager.command_test.Command;
import us.nebula.api.manager.command_test.CommandManifest;
import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.parser.CommandContext;

/**
 * @author xgraza
 * @since 08/13/25
 */
@CommandManifest(
        aliases = {"friend", "f", "fren"},
        description = "Adds or removes a player from your friends list")
public final class FriendCommand extends Command
{
    public FriendCommand()
    {
        ;
    }

    @Override
    public CommandResult dispatch(CommandContext commandContext)
    {
        return null;
    }
}
