package us.nebula.impl.command;

import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.xgraza.xcmd.executor.CommandResult;
import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.CommandContext;
import us.xgraza.xcmd.parser.argument.internal.ArgumentCommand;
import us.xgraza.xcmd.registry.CommandRegistry;

import java.util.StringJoiner;

/**
 * @author xgraza
 * @since 08/13/25
 */
@CommandManifest(
        aliases = { "help", "h", "cmds", "commands" },
        description = "Displays a list of commands and other information")
public final class HelpCommand extends Command
{
    private final CommandRegistry registry;

    public HelpCommand(final CommandRegistry registry)
    {
        this.registry = registry;
        registerArgument(ArgumentCommand.command(registry, "command-name")
                .setRequired(false));
    }

    @Override
    public CommandResult dispatch(final CommandContext ctx)
    {
        if (ctx.hasArgument("command-name"))
        {
            final ICommandExecutor executor = ctx.getArgument("command-name");
            final StringBuilder builder = new StringBuilder();
            builder.append("&lAliases&r: ");
            builder.append(String.join(", ", executor.getAliases()));
            if (executor instanceof Command)
            {
                final Command command = (Command) executor;
                builder.append("\n");
                builder.append("&lDescription&r: ");
                builder.append(command.getDescription());

                final String syntax = command.getSyntax();
                if (syntax != null && !syntax.isEmpty())
                {
                    builder.append("\n");
                    builder.append("&lSyntax&r: ");
                    builder.append(command.getSyntax());
                }
            }
            return ctx.ok(builder.toString());
        }
        final StringJoiner joiner = new StringJoiner(", ");
        for (final ICommandExecutor executor : registry.getExecutors())
        {
            joiner.add(executor.getAliases()[0]);
        }
        return ctx.ok("Commands (" + registry.getExecutors().size() + "): " + joiner);
    }
}
