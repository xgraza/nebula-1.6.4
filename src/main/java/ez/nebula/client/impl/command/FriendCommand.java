package ez.nebula.client.impl.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;

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
        literal.then(literal("add")
                .then(argument("name", StringArgumentType.word())
                        .executes((ctx) ->
                        {
                            final String name = StringArgumentType.getString(ctx, "name");
                            if (Nebula.FRIENDS.isFriend(name))
                            {
                                return ctx.getSource().respond("You already have %s friended!", name);
                            }
                            Nebula.FRIENDS.addFriend(name);
                            MC.thePlayer.sendChatMessage("/msg " + name + " I just added you as a friend on Nebula!");
                            return ctx.getSource().respond("You are now friends with %s!", name);
                        })))
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.word())
                                .executes((ctx) ->
                                {
                                    final String name = StringArgumentType.getString(ctx, "name");
                                    if (!Nebula.FRIENDS.isFriend(name))
                                    {
                                        return ctx.getSource().respond("You are not friends with %s.", name);
                                    }
                                    Nebula.FRIENDS.removeFriend(name);
                                    return ctx.getSource().respond("You are now no longer friends with %s.", name);
                                })))
                .executes((ctx) ->
                {
                    final List<String> friends = Nebula.FRIENDS.getAll();
                    if (friends.isEmpty())
                    {
                        return ctx.getSource().respond("You don't have any friends :(");
                    }
                    return ctx.getSource().respond("You have %s friend(s): %s",
                            friends.size(), String.join(", ", friends));
                });
    }
}
