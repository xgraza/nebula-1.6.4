package us.nebula.api.manager.command.argument;

import java.util.*;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class ArgumentBuilder
{
    private final Map<String, Argument<?>> argumentMap = new LinkedHashMap<>();
    private final List<Argument<?>> argumentList = new LinkedList<>();
    private CommandDispatcher commandDispatcher;

    public void dispatchSingle(final CommandDispatcher commandDispatcher)
    {
        this.commandDispatcher = commandDispatcher;
    }

    public ArgumentBuilder dependantArgument(final Argument<?> argument, final String dependant)
    {
        final Argument<?> arg = argumentMap.get(dependant);
        if (arg == null)
        {
            //throw new RuntimeException("Could not find dependant \"%s\"".formatted(dependant));
        }
        argument.setDependant(true);
        arg.addDependant(argument);
        argumentList.add(argument);
        return this;
    }

    public <T> ArgumentBuilder argument(final Argument<T> argument, final ArgumentDispatcher<T> dispatcher)
    {
        argument.setArgumentExecutor(dispatcher);
        argumentMap.put(argument.getName(), argument);
        argumentList.add(argument);
        return this;
    }

    public ArgumentBuilder argument(final Argument<?> argument)
    {
        argument.setRequired(false);
        argumentMap.put(argument.getName(), argument);
        argumentList.add(argument);
        return this;
    }

    public void clearArgumentValues()
    {
        for (final Argument<?> argument : argumentMap.values())
        {
            argument.clearValue();
            for (final Argument<?> dependantArgument : argument.getDependants())
            {
                dependantArgument.clearValue();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Argument<T> getArgument(final String name)
    {
        return (Argument<T>) argumentMap.get(name);
    }

    public List<Argument<?>> getArguments()
    {
        return argumentList;
    }

    public CommandDispatcher getCommandDispatcher()
    {
        return commandDispatcher;
    }
}
