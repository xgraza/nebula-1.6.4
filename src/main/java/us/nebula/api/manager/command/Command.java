package us.nebula.api.manager.command;

import net.minecraft.client.Minecraft;
import us.nebula.api.manager.command.argument.Argument;
import us.nebula.api.manager.command.argument.ArgumentBuilder;

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 1.0.0
 */
public abstract class Command
{
    static final String DEFAULT_DESCRIPTION = "No description provided for this command";
    protected static final Minecraft MC = Minecraft.getMinecraft();

    protected final ArgumentBuilder argumentBuilder = new ArgumentBuilder();
    private final CommandManifest manifest;
    private String syntax;

    public Command()
    {
        if (!getClass().isAnnotationPresent(CommandManifest.class))
        {
            throw new RuntimeException("@CommandManifest must be present on top of a command");
        }
        manifest = getClass().getDeclaredAnnotation(CommandManifest.class);
    }

    public abstract void build();

    public void generateSyntax()
    {
        if (syntax != null)
        {
            return;
        }
        final StringJoiner joiner = new StringJoiner(" ");
        for (final Argument<?> argument : argumentBuilder.getArguments())
        {
            final List<Argument<?>> dependants = argument.getDependants();
            if (dependants.isEmpty())
            {
                joiner.add(
                        (argument.isRequired() ? "[" : "<") +
                                argument.getName() +
                                (argument.isRequired() ? "]" : ">"));
            } else
            {
                joiner.add(
                        (argument.isRequired() ? "[" : "<") +
                                argument.getName() +
                                "?" +
                                dependants.stream().map(Argument::getName).collect(Collectors.joining("|")) +
                                (argument.isRequired() ? "]" : ">"));
            }
        }
        syntax = joiner.toString();
    }

    public ArgumentBuilder getArgumentBuilder()
    {
        return argumentBuilder;
    }

    public CommandManifest getManifest()
    {
        return manifest;
    }

    public String getSyntax()
    {
        return syntax;
    }
}
