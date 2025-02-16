package us.nebula.api.manager.command;

import net.minecraft.network.play.client.C01PacketChatMessage;
import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.ITypedManager;
import us.nebula.api.manager.command.exception.CommandParseException;
import us.nebula.impl.command.SetUsernameCommand;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.ChatUtil;

import java.util.*;

/**
 * @author xgraza
 * @since 4.0.0
 */
@SuppressWarnings("unchecked")
public final class CommandManager implements ITypedManager<Command>
{
    private static final String COMMAND_PREFIX = ".";

    private final Map<String, Command> commandAliasMap = new HashMap<>();
    private final List<Command> commandList = new LinkedList<>();

    private final CommandParser commandParser = new CommandParser(
            this, COMMAND_PREFIX);

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C01PacketChatMessage)
        {
            final C01PacketChatMessage packet = event.getPacket();
            final String message = packet.getMessage();
            if (!message.startsWith(COMMAND_PREFIX))
            {
                return;
            }
            event.cancel();
            try
            {
                commandParser.parse(message);
            } catch (final CommandParseException e)
            {
                throw new RuntimeException(e);
            } catch (final Exception e)
            {

            }
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);

        addCommand(new SetUsernameCommand());

        commandParser.onCommandNotFound((commandName) ->
        {
            ChatUtil.send("Could not resolve a command with the name {}", commandName);
        });
        commandParser.onInvalidSyntax((syntax) ->
        {
            ChatUtil.send("The proper syntax is: {}", syntax);
        });
    }

    public void addCommand(final Command command)
    {
        command.build();
        command.generateSyntax();
        for (final String alias : command.getManifest().aliases())
        {
            commandAliasMap.put(alias, command);
        }
        commandList.add(command);
    }

    public <T extends Command> T getReference(final String alias)
    {
        return (T)commandAliasMap.get(alias);
    }

    @Override
    public List<Command> getAll()
    {
        return Collections.emptyList();
    }
}
