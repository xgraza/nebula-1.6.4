package us.nebula.client.api.manager.command;

import net.minecraft.client.Minecraft;
import us.xgraza.xcmd.executor.ICommandExecutor;
import us.xgraza.xcmd.parser.argument.Argument;
import us.xgraza.xcmd.parser.flag.Flag;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 08/12/25
 */
public abstract class Command implements ICommandExecutor
{
    static final String DEFAULT_DESCRIPTION = "No description provided for this command";
    protected static final Minecraft MC = Minecraft.getMinecraft();

    private final CommandManifest manifest;
    private final List<Argument<?>> arguments = new LinkedList<>();
    private final List<Flag<?>> flags = new LinkedList<>();
    private String syntax;

    public Command()
    {
        if (!getClass().isAnnotationPresent(CommandManifest.class))
        {
            throw new RuntimeException("@CommandManifest must be present on top of a command");
        }
        manifest = getClass().getDeclaredAnnotation(CommandManifest.class);
    }

    private void generateSyntax()
    {
        final StringBuilder builder = new StringBuilder();
        if (!arguments.isEmpty())
        {
            for (final Argument<?> argument : arguments)
            {
                final boolean required = argument.isRequired();
                builder.append(required ? "[" : "<");
                builder.append(argument.getName());
                builder.append(":");
                builder.append(argument.getTokenType());
                builder.append(required ? "]" : ">");
                builder.append(" ");
            }
        }
        if (!flags.isEmpty())
        {
            for (final Flag<?> flag : flags)
            {
                builder.append("-");
                builder.append(flag.getName());
                builder.append(":");
                builder.append(flag.getArgument().getTokenType());
                builder.append(" ");
            }
        }
        syntax = builder.toString();
    }

    protected void registerArgument(final Argument<?> argument)
    {
        arguments.add(argument);
    }

    protected void registerFlag(final Flag<?> flag)
    {
        flags.add(flag);
    }

    @Override
    public List<Argument<?>> getArguments()
    {
        return arguments;
    }

    @Override
    public List<Flag<?>> getFlags()
    {
        return flags;
    }

    @Override
    public String[] getAliases()
    {
        return manifest.aliases();
    }

    public String getDescription()
    {
        return manifest.description();
    }

    public String getSyntax()
    {
        if (syntax == null)
        {
            generateSyntax();
        }
        return syntax;
    }
}
