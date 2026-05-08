package us.nebula.client.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.CheatManager;
import us.nebula.client.command.trait.CommandSource;

import java.util.concurrent.CompletableFuture;

public final class CheatArgument implements ArgumentType<Cheat>
{
    private final CheatManager cheatManager;

    public CheatArgument(final CheatManager cheatManager)
    {
        this.cheatManager = cheatManager;
    }

    @Override
    public Cheat parse(final StringReader reader) throws CommandSyntaxException
    {
        final String target = reader.readString().toLowerCase();
        for (final Cheat cheat : cheatManager.getAll())
        {
            final String name = cheat.getManifest().name().toLowerCase();
            if (target.equalsIgnoreCase(name) || target.startsWith(name))
            {
                return cheat;
            }
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        final String input = builder.getRemaining().toLowerCase();
        for (final Cheat cheat : cheatManager.getAll())
        {
            final String name = cheat.getManifest().name().toLowerCase();
            if (input.equals(name) || name.startsWith(input) || name.contains(input))
            {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    public static Cheat get(final CommandContext<CommandSource> ctx, final String name)
    {
        return ctx.getArgument(name, Cheat.class);
    }

    public static CheatArgument cheat()
    {
        return new CheatArgument(Nebula.INSTANCE.getCheatManager());
    }
}
