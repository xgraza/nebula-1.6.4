package ez.nebula.client.api.manager.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.Minecraft;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;

public abstract class Command
{
    public static final String DEFAULT_DESCRIPTION = "No description provided for this command";
    protected static final Minecraft MC = Minecraft.getMinecraft();

    private final CommandManifest manifest;

    public Command()
    {
        manifest = getClass().getDeclaredAnnotation(CommandManifest.class);
        if (manifest == null)
        {
            throw new RuntimeException(
                    "@CommandManifest needs to be annotated on top of a Command class");
        }
    }

    public abstract void createBuilder(final LiteralArgumentBuilder<CommandSource> literal);

    protected <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> argumentType)
    {
        return RequiredArgumentBuilder.argument(name, argumentType);
    }

    public static LiteralArgumentBuilder<CommandSource> literal(final String alias)
    {
        return LiteralArgumentBuilder.literal(alias);
    }

    public CommandManifest getManifest()
    {
        return manifest;
    }

    public String[] getAliases()
    {
        return manifest.aliases();
    }

    public String getDescription()
    {
        return manifest.description();
    }

    public boolean isVisible()
    {
        return true;
    }
}
