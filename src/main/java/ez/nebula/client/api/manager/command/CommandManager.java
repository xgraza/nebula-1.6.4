package ez.nebula.client.api.manager.command;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.input.EventKey;
import ez.nebula.client.api.manager.ITypedManager;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.impl.command.*;
import ez.nebula.client.util.minecraft.player.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public final class CommandManager implements ITypedManager<Command>
{
    private static final Logger LOGGER = LogManager.getLogger("Commands");
    private static final Minecraft MC = Minecraft.getMinecraft();
    public static final String COMMAND_PREFIX = ".";

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();
    private final List<Command> commandInstanceList = new ArrayList<>();
    private final Map<String, Command> commandInstanceMap = new HashMap<>();

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (event.getKeyCode() == Keyboard.KEY_PERIOD && MC.currentScreen == null)
        {
            MC.displayGuiScreen(new GuiChat());
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        register(new BindCommand());
        register(new DownloadMapCommand());
        register(new FriendCommand());
        register(new HelpCommand());
        // register(new PathToCommand());
        register(new PingCommand());
        register(new SelfKickCommand());
        register(new SpawnTPCommand());
        register(new ToggleCommand());
        register(new WaypointCommand());

        LOGGER.info("Registered {} commands", commandInstanceList.size());
    }

    public void register(final Command command)
    {
        commandInstanceList.add(command);
        for (final String alias : command.getAliases())
        {
            commandInstanceMap.put(alias, command);
            final LiteralArgumentBuilder<CommandSource> literal = Command.literal(alias);
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
                case CommandSource.SUCCESS:
                {
                    break;
                }
            }
        } catch (final Exception e)
        {
            LOGGER.error("Failed to execute command", e);
            ChatUtil.sendNebula("&cAn error occurred while executing the command:");
            ChatUtil.sendNebula("&c%s", e.getMessage());
        }
    }

    public CommandNode<CommandSource> getLastCommandNode(final ParseResults<CommandSource> parseResults)
    {
        return parseResults.getContext().getNodes().isEmpty()
                ? parseResults.getContext().getRootNode()
                : Iterables.getLast(parseResults.getContext().getNodes()).getNode();
    }

    public void addSuggestions(final ParseResults<CommandSource> parseResults, final List<String> list)
    {
        final CompletableFuture<Suggestions> future = dispatcher.getCompletionSuggestions(parseResults);
        final Suggestions suggestions = future.getNow(null);
        list.clear();
        for (final Suggestion suggestion : suggestions.getList())
        {
            list.add(suggestion.getText());
        }
    }

    public Collection<String> getSmartUsages(final Command command, final CommandSource src)
    {
        final String input = COMMAND_PREFIX + command.getAliases()[0];
        final ParseResults<CommandSource> parseResults = parse(input);
        if (parseResults == null)
        {
            return Collections.emptySet();
        }
        return dispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), createSource(input)).values();
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

    @Override
    public List<Command> getAll()
    {
        return commandInstanceList;
    }
}
