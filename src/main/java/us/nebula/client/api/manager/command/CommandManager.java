package us.nebula.client.api.manager.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import us.nebula.client.api.manager.IManager;
import us.nebula.client.impl.command.PingCommand;
import us.nebula.client.impl.command.SpawnTPCommand;
import us.nebula.client.impl.command.ToggleCommand;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class CommandManager implements IManager
{
    public static final String COMMAND_PREFIX = ".";

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final List<Command> commandInstanceList = new ArrayList<>();
    private final Map<String, Command> commandInstanceMap = new HashMap<>();

    @Override
    public void init()
    {
        register(new PingCommand());
        register(new SpawnTPCommand());
        register(new ToggleCommand());
    }

    public void register(final Command command)
    {
        commandInstanceList.add(command);
        for (final String alias : command.getManifest().aliases())
        {
            commandInstanceMap.put(alias, command);
            final LiteralArgumentBuilder<CommandSource> literal = LiteralArgumentBuilder.literal(alias);
            command.createBuilder(literal);
            dispatcher.register(literal);
        }
    }

    public CommandSource createSource(String input)
    {
        if (input == null || !input.startsWith(COMMAND_PREFIX))
        {
            return null;
        }
        input = input.substring(COMMAND_PREFIX.length());

        final String[] arguments = input.split(" ");
        if (arguments.length == 0)
        {
            return null;
        }
        final String commandName = arguments[0];
        final Command command = commandInstanceMap.get(commandName);
        return new CommandSource(command, input, this);
    }

    public ParseResults<CommandSource> parse(final String input)
    {
        final CommandSource source = createSource(input);
        if (source == null)
        {
            return null;
        }
        return dispatcher.parse(source.getRawInput(), source);
    }

    public void execute(final ParseResults<CommandSource> parseResults)
    {
        try
        {
            int result = dispatcher.execute(parseResults);
            switch (result)
            {

            }
        } catch (CommandSyntaxException e)
        {

        }
    }

    public CommandNode<CommandSource> getLastCommandNode(final ParseResults<CommandSource> parseResults)
    {
        return parseResults.getContext().getNodes().isEmpty()
                ? parseResults.getContext().getRootNode()
                : Iterables.getLast(parseResults.getContext().getNodes()).getNode();
    }

    public List<String> getSuggestions(final ParseResults<CommandSource> parseResults)
    {
        final CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(parseResults);
        final Suggestions suggestions = future.getNow(null);
        final List<String> suggestionList = new ArrayList<>();
        for (final Suggestion suggestion : suggestions.getList())
        {
            suggestionList.add(suggestion.getText());
        }
        return suggestionList;
    }

    public String getSmartUsage(final ParseResults<CommandSource> parseResults)
    {
        final List<ParsedCommandNode<CommandSource>> nodeList = parseResults.getContext().getNodes();
        if (nodeList.isEmpty())
        {
            return null;
        }
        final CommandNode<CommandSource> node = Iterables.getLast(nodeList).getNode();

        final String content = parseResults.getReader().getString();
        final Map<CommandNode<CommandSource>, String> smartUsages = dispatcher.getSmartUsage(
                node, createSource(content));
        return smartUsages.values().stream().findFirst().orElse(null);
    }

    public CommandDispatcher<CommandSource> getDispatcher()
    {
        return dispatcher;
    }
}
