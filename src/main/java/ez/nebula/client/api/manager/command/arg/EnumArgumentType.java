package ez.nebula.client.api.manager.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ez.nebula.client.api.manager.command.trait.CommandSource;

/**
 * @author xgraza
 * @since 6/2/26
 * @param <T> the enum type
 */
public final class EnumArgumentType<T extends Enum<?>> implements ArgumentType<T>
{
    private final T[] values;

    public EnumArgumentType(final T[] values)
    {
        this.values = values;
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException
    {
        final String target = reader.readString().toLowerCase();
        for (final T value : values)
        {
            if (target.equalsIgnoreCase(value.toString()))
            {
                return value;
            }
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(reader);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T get(final CommandContext<CommandSource> ctx, final String name)
    {
        return (T) ctx.getArgument(name, Enum.class);
    }

    public static <T extends Enum<?>> EnumArgumentType<T> enumArg(final T[] values)
    {
        return new EnumArgumentType<>(values);
    }
}
