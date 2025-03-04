package us.nebula.impl.command;

import us.nebula.Nebula;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;
import us.nebula.api.manager.command.argument.type.CommandArgument;
import us.nebula.util.player.ChatUtil;

import java.util.List;
import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CommandManifest(aliases = {"help", "cmd", "commands"})
public final class HelpCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder.argument(
                new CommandArgument("command")
                        .setRequired(false), (arg) ->
                {
                    final Command command = arg.getValue();
                    ChatUtil.send("Aliases: %s\nDescription: %s\nSyntax: %s",
                            String.join(", ", command.getManifest().aliases()),
                            command.getManifest().description(),
                            command.getSyntax());
                    return CommandResult.SUCCESS;
                })
                .dispatchSingle(() ->
                {
                    final List<Command> commands = Nebula.INSTANCE.getCommandManager().getAll();
                    final StringJoiner joiner = new StringJoiner(", ");
                    for (final Command command : commands)
                    {
                        joiner.add(command.getManifest().aliases()[0]);
                    }
                    ChatUtil.send("Commands(%s): %s", commands.size(), joiner.toString());
                    return CommandResult.SUCCESS;
                });
    }
}
