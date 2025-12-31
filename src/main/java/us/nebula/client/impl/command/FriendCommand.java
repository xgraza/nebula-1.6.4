package us.nebula.client.impl.command;

import us.nebula.client.Nebula;
import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.internal.ArgumentEnum;
import us.xgraza.xcmd.parser.argument.internal.ArgumentString;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 08/13/25
 */
@CommandManifest(
        aliases = { "friend", "f", "fren" },
        description = "Adds or removes a player from your friends list")
public final class FriendCommand extends Command
{
    public FriendCommand()
    {
        registerArgument(new ArgumentEnum<>(Option.class, "option"));
        registerArgument(ArgumentString.greedy("id")
                .setRequired(false));
    }

    @Override
    public CommandResult dispatch(final CommandContext ctx)
    {
        final Option option = ctx.getArgument("option");
        if (option == Option.LIST)
        {
            final List<String> friendList = Nebula.INSTANCE.getFriendManager().getAll();
            if (friendList.isEmpty())
            {
                return ctx.ok("You have no friends :(");
            }
            final StringJoiner joiner = new StringJoiner(", ");
            friendList.forEach(joiner::add);
            return ctx.ok("Friends (" + friendList.size() + "): " + joiner);
        } else
        {
            if (!ctx.hasArgument("id"))
            {
                return ctx.fail("need id");
            }
            final String id = ctx.getArgument("id");
            if (option == Option.ADD)
            {
                Nebula.INSTANCE.getFriendManager().addFriend(id);
            } else
            {
                Nebula.INSTANCE.getFriendManager().removeFriend(id);
            }
            return ctx.ok(
                    (option == Option.ADD ? "Added" : "Removed") + " " +
                            id + " ");
        }
    }

    private enum Option
    {
        ADD, REMOVE, LIST
    }
}
