package ez.nebula.client.api.manager.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.CommandManager;
import ez.nebula.client.api.manager.command.trait.CommandSource;

public class CommandArgumentType implements ArgumentType<Command>
{
    private final CommandManager manager;

    public CommandArgumentType(final CommandManager manager)
    {
        this.manager = manager;
    }

    @Override
    public Command parse(StringReader reader) throws CommandSyntaxException
    {
        final String target = reader.readString().toLowerCase();
        for (final Command command : manager.getAll())
        {
            for (final String alias : command.getAliases())
            {
                if (target.contains(alias))
                {
                    return command;
                }
            }
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(reader);
    }

    public static Command get(final CommandContext<CommandSource> ctx, final String name)
    {
        return ctx.getArgument(name, Command.class);
    }

    public static CommandArgumentType command()
    {
        return new CommandArgumentType(Nebula.COMMANDS);
    }
}
