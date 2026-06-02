package ez.nebula.client.api.manager.command.arg;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.ModuleManager;
import ez.nebula.client.api.manager.command.trait.CommandSource;

import java.util.concurrent.CompletableFuture;

public final class ModuleArgument implements ArgumentType<Module>
{
    private final ModuleManager moduleManager;

    public ModuleArgument(final ModuleManager moduleManager)
    {
        this.moduleManager = moduleManager;
    }

    @Override
    public Module parse(final StringReader reader) throws CommandSyntaxException
    {
        final String target = reader.readString().toLowerCase();
        for (final Module module : moduleManager.getAll())
        {
            final String name = module.getManifest().name().toLowerCase();
            if (target.equalsIgnoreCase(name) || target.startsWith(name))
            {
                return module;
            }
        }
        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument()
                .createWithContext(reader);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder)
    {
        final String input = builder.getRemaining().toLowerCase();
        for (final Module module : moduleManager.getAll())
        {
            final String name = module.getManifest().name().toLowerCase();
            if (input.equals(name) || name.startsWith(input) || name.contains(input))
            {
                builder.suggest(name);
            }
        }
        return builder.buildFuture();
    }

    public static Module get(final CommandContext<CommandSource> ctx, final String name)
    {
        return ctx.getArgument(name, Module.class);
    }

    public static ModuleArgument module()
    {
        return new ModuleArgument(Nebula.INSTANCE.getModuleManager());
    }
}
