package us.nebula.api.manager.command.argument.type;

import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.argument.Argument;

// TODO; this is retarded
public class Arguments
{
    public static <T extends Number> Argument<T> number(Class<T> type, String name)
    {
        return new NumberArgument<>(type, name);
    }

    public static Argument<String> string(final String name)
    {
        return new StringArgument(name);
    }

    public static Argument<Command> command(final String name)
    {
        return new CommandArgument(name);
    }
}
