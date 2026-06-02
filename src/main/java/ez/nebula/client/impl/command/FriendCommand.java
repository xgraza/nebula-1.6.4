package ez.nebula.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.arg.EnumArgument;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.core.Nebula;

import java.util.List;

/**
 * @author xgraza
 * @since 6/2/26
 */
@CommandManifest(aliases = { "friend", "friends", "f" },
        description = "Adds, removes, or lists your friends")
public final class FriendCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(argument("option", EnumArgument.enumArg(Option.values()))
                .then(argument("name", StringArgumentType.word())
                .executes((ctx) ->
                {
                    final Option option = EnumArgument.get(ctx, "option");
                    final String name = StringArgumentType.getString(ctx, "name");
                    if (option == Option.ADD)
                    {
                        if (Nebula.INSTANCE.getFriendManager().isFriend(name))
                        {
                            return ctx.getSource().respond("You already have %s friended!", name);
                        }
                        Nebula.INSTANCE.getFriendManager().addFriend(name);
                        MC.thePlayer.sendChatMessage("/msg " + name + " I just added you as a friend on Nebula!");
                        return ctx.getSource().respond("You are now friends with %s!", name);
                    } else if (option == Option.REMOVE)
                    {
                        if (!Nebula.INSTANCE.getFriendManager().isFriend(name))
                        {
                            return ctx.getSource().respond("You are not friends with %s.", name);
                        }
                        Nebula.INSTANCE.getFriendManager().removeFriend(name);
                        return ctx.getSource().respond("You are now no longer friends with %s.", name);
                    }
                    return ctx.getSource().respond("what?");
                })))
                .executes((ctx) ->
                {
                    final List<String> friends = Nebula.INSTANCE.getFriendManager().getAll();
                    if (friends.isEmpty())
                    {
                        return ctx.getSource().respond("You don't have any friends :(");
                    }
                    return ctx.getSource().respond("You have %s friend(s): %s",
                            friends.size(), String.join(", ", friends));
                });
    }

    private enum Option
    {
        ADD, REMOVE
    }
}
