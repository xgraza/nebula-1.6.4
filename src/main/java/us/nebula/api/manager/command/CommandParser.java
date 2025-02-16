package us.nebula.api.manager.command;

import us.nebula.api.manager.command.argument.*;
import us.nebula.api.manager.command.event.CommandDispatchEvent;
import us.nebula.api.manager.command.exception.ArgumentResolveException;
import us.nebula.api.manager.command.event.CommandNotFoundEvent;
import us.nebula.api.manager.command.event.InvalidSyntaxEvent;
import us.nebula.api.manager.command.exception.CommandParseException;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xgraza
 * @since 1.0.0
 *
 * whats that? whats a father? heh...
 */
@SuppressWarnings("all")
public final class CommandParser
{
    private static final Pattern PHRASE_REGEX = Pattern.compile("[\"'](.*?)[\"']|(\\S+)");
    private static final Pattern DOUBLE_QUOTE_REGEX = Pattern.compile("(\"{2,})|('{2,})");
    private static final Pattern DECIMAL_REGEX = Pattern.compile("-?\\d+\\.\\d+");
    private static final Pattern NUMBER_REGEX = Pattern.compile("-?\\d+");

    private final CommandManager commandManager;
    private final String commandPrefix;

    private CommandDispatchEvent commandDispatchEvent;
    private CommandNotFoundEvent commandNotFoundEvent;
    private InvalidSyntaxEvent invalidSyntaxEvent;

    public CommandParser(final CommandManager commandManager, final String commandPrefix)
    {
        this.commandManager = commandManager;
        this.commandPrefix = commandPrefix;
    }

    public void parse(final String input) throws Exception
    {
        // Purify input
        String cleaned = input.trim().substring(commandPrefix.length());
        if (cleaned.isEmpty())
        {
            return;
        }

        final String commandName = cleaned.split(" ")[0];
        final Command command = commandManager.getReference(commandName.toLowerCase());
        if (command == null)
        {
            // TODO: find similar commands to suggest?
            commandNotFoundEvent.execute(commandName);
            return;
        }

        final ArgumentBuilder argumentBuilder = command.getArgumentBuilder();
        final List<Argument<?>> args = argumentBuilder.getArguments();
        argumentBuilder.clearArgumentValues();

        // Establish how many arguments are required
        int required = 0;
        for (final Argument<?> argument : args)
        {
            if (argument.isRequired())
            {
                ++required;
            }
        }

        cleaned = cleaned.substring(commandName.length());
        final List<String> tokens = tokenize(cleaned);
        final List<Class<?>> types = lexer(tokens);

        // If the amount of required arguments are at least less than the required, we are missing arguments
        if (required > tokens.size())
        {
            invalidSyntaxEvent.execute(command.getSyntax());
            return;
        }

        for (int i = 0; i < tokens.size(); ++i)
        {
            if (i + 1 > args.size())
            {
                continue;
            }
            final String token = tokens.get(i);
            final Argument<?> argument = args.get(i);
            if (!argument.getDependants().isEmpty())
            {
                i = resolveDependants(argument, i, tokens, types);
            }

            resolveArgument(argument, token);
            if (!argument.isDependant() && argument.isResolved())
            {
                final ArgumentDispatcher dispatcher = argument.getArgumentExecutor();
                if (dispatcher != null)
                {
                    commandDispatchEvent.execute(command, dispatcher.dispatch(argument));
                    return;
                }
            }
        }

        final CommandDispatcher dispatcher = argumentBuilder.getCommandDispatcher();
        if (dispatcher != null)
        {
            commandDispatchEvent.execute(command, dispatcher.dispatch());
        }
    }

    private List<Class<?>> lexer(final List<String> tokens)
    {
        final List<Class<?>> lexed = new LinkedList<>();
        for (final String arg : tokens)
        {
            lexed.add(parseType(arg));
        }
        return lexed;
    }

    private Class<?> parseType(String arg)
    {
        if (arg == null || arg.isEmpty())
        {
            return null;
        }

        // Check for number
        if (arg.contains("\\.") && arg.matches(DECIMAL_REGEX.pattern()))
        {
            if (arg.endsWith("f") || arg.endsWith("F"))
            {
                return Float.class;
            } else
            {
                return Double.class;
            }
        } else if (arg.matches(NUMBER_REGEX.pattern()))
        {
            if (arg.endsWith("L"))
            {
                return Long.class;
            }

            return Integer.class;
        }

        // Check for boolean
        if (arg.equalsIgnoreCase("true") || arg.equalsIgnoreCase("false"))
        {
            return Boolean.class;
        }

        return String.class;
    }

    private List<String> tokenize(final String input)
    {
        final List<String> tokens = new LinkedList<>();
        final Matcher matcher = PHRASE_REGEX.matcher(input);
        while (matcher.find())
        {
            String value;
            // sometimes my own genius scares me...
            if ((value = matcher.group(1)) != null)
            {
            } else
            {
                value = matcher.group(0);
            }
            if (!value.isEmpty())
            {
                tokens.add(value);
            }
        }
        return tokens;
    }

    private int resolveDependants(final Argument<?> argument,
                                  final int index,
                                  final List<String> tokens,
                                  final List<Class<?>> types)
            throws CommandParseException
    {
        final List<Argument<?>> dependants = argument.getDependants();
        if (dependants.isEmpty())
        {
            return -1;
        }

        // Lookahead and see if we have enough raw arguments to parse for the dependants
        if (index + 1 + dependants.size() > tokens.size())
        {
            return -1;
        }

        int j = 0;
        for (int i = index + 1; i < dependants.size(); i++)
        {
            final Argument<?> dependant = dependants.get(j);
            checkTypes(dependant, types.get(i));
            resolveArgument(dependant, tokens.get(i));
            ++j;
        }

        return index + 1 + j;
    }

    private void checkTypes(final Argument<?> argument, final Class<?> type)
            throws CommandParseException
    {
        final Class<?> argumentType = argument.getType();
        if (!type.isAssignableFrom(argumentType))
        {
            throw new CommandParseException(String.format("Mismatched types. %s != %s",
                    type.getSimpleName(), argumentType.getSimpleName()));
        }
    }

    private void resolveArgument(final Argument<?> argument, final String raw)
            throws CommandParseException
    {
        try
        {
            checkForConstraints(argument, raw);
            argument.resolve(raw);
        } catch (final ArgumentResolveException e)
        {
            // Possible that this specific argument type was not supplied because it was optional?
            if (argument.isRequired())
            {
                throw new CommandParseException(String.format("Could not parse argument %s",
                        argument.getName()));
            }
        }
    }

    private void checkForConstraints(final Argument<?> argument, final String raw)
            throws CommandParseException
    {
        final String failReason = argument.passes(raw);
        if (failReason == null || failReason.isEmpty())
        {
            return;
        }
        throw new CommandParseException(failReason);
    }

    public void onCommandDispatch(final CommandDispatchEvent commandDispatchEvent)
    {
        this.commandDispatchEvent = commandDispatchEvent;
    }

    public void onCommandNotFound(final CommandNotFoundEvent commandNotFoundEvent)
    {
        this.commandNotFoundEvent = commandNotFoundEvent;
    }

    public void onInvalidSyntax(final InvalidSyntaxEvent invalidSyntaxEvent)
    {
        this.invalidSyntaxEvent = invalidSyntaxEvent;
    }
}
