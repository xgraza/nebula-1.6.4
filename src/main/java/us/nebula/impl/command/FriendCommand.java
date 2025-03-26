package us.nebula.impl.command;

import us.nebula.Nebula;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.StringArgument;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/26/25
 */
@CommandManifest(aliases = {"friend", "f", "fren"},
        description = "Friends or unfriends a player")
public final class FriendCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder
                .argument(new StringArgument("name",
                        StringArgument.minMax(1, 16)), (arg) ->
                {
                    final String username = arg.getValue();
                    final boolean friended = Nebula.INSTANCE.getFriendManager().isFriend(username);
                    if (friended)
                    {
                        Nebula.INSTANCE.getFriendManager().removeFriend(username);
                    } else
                    {
                        Nebula.INSTANCE.getFriendManager().addFriend(username);
                    }
                    ChatUtil.send("%s &b%s&r as a friend",
                            !friended ? "Added" : "Removed",
                            username);
                    return CommandResult.SUCCESS;
                });
    }
}
