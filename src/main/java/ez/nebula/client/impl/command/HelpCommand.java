package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.CommandManager;
import ez.nebula.client.api.manager.command.arg.CommandArgumentType;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.minecraft.player.ChatUtil;

import java.util.Collection;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 6/12/26
 */
@CommandManifest(aliases = {"help"}, description = "Displays command usages")
public final class HelpCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.then(argument("alias", CommandArgumentType.command())
                .executes((ctx) ->
                {
                    final Command command = CommandArgumentType.get(ctx, "alias");
                    ChatUtil.sendNebula("- Usages:");
                    for (final String usage : Nebula.INSTANCE.getCommandManager().getSmartUsages(command, ctx.getSource()))
                    {
                        ChatUtil.sendNebula("  " + CommandManager.COMMAND_PREFIX + command.getAliases()[0] + " " + usage);
                    }
                    ChatUtil.sendNebula("- Description: %s", command.getDescription());
                    return ctx.getSource().respond("- Aliases: %s", String.join(", ", command.getAliases()));
                }))
                .executes((ctx) ->
                {
                    final Collection<Command> commandList = Nebula.INSTANCE.getCommandManager().getAll()
                            .stream()
                            .filter(Command::isVisible)
                            .collect(Collectors.toList());
                    final StringJoiner joiner = new StringJoiner(", ");
                    for (final Command command : commandList)
                    {
                        joiner.add(command.getAliases()[0]);
                    }
                    return ctx.getSource().respond("Commands (%s): %s", commandList.size(), joiner);
                });
    }
}
