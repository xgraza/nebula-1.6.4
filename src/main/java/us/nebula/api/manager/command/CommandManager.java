package us.nebula.api.manager.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.network.play.client.C01PacketChatMessage;
import us.nebula.Nebula;
import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.ITypedManager;
import us.nebula.api.manager.command.exception.CommandParseException;
import us.nebula.impl.command.HelpCommand;
import us.nebula.impl.command.SetUsernameCommand;
import us.nebula.impl.command.SpawnTPCommand;
import us.nebula.impl.event.input.EventKey;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.ChatUtil;

import java.util.*;

import static org.lwjgl.input.Keyboard.KEY_PERIOD;

/**
 * @author xgraza
 * @since 4.0.0
 */
@SuppressWarnings("unchecked")
public final class CommandManager implements ITypedManager<Command>
{
    private static final Minecraft MC = Minecraft.getMinecraft();
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
                ChatUtil.send("&c%s", e.getMessage());
                Nebula.INSTANCE.getLogger().error(e.getMessage());
            } catch (final Exception e)
            {
                ChatUtil.send("A fatal exception occurred while executing the command. " +
                        "Check console and report to my developers!" +
                        " https://github.com/xgraza/nebula-1.6.4");
                Nebula.INSTANCE.getLogger().error(e);
            }
        }
    };

    @Subscribe
    private final EventListener<EventKey> keyEventListener = event ->
    {
        if (event.getKeyCode() == KEY_PERIOD && MC.currentScreen == null)
        {
            MC.displayGuiScreen(new GuiChat());
        }
    };

    @Override
    public void init()
    {
        EventBus.subscribe(this);

        addCommand(new HelpCommand());
        addCommand(new SetUsernameCommand());
        addCommand(new SpawnTPCommand());

        commandParser.onCommandDispatch((command, result) ->
        {
            switch (result)
            {
                case CommandResult.FAIL:
                    ChatUtil.send("Command failed to execute");
                    break;
                case CommandResult.SUCCESS:
                    break;
                case CommandResult.SUCCESS_DEFAULT:
                    ChatUtil.send("Command dispatched successfully");
                    break;
            }
        });
        commandParser.onCommandNotFound((commandName) ->
        {
            ChatUtil.send("There is no command with the name %s. " +
                    "Please run the help command (.help)", commandName);
        });
        commandParser.onInvalidSyntax((syntax) ->
        {
            ChatUtil.send("The proper syntax is: %s", syntax);
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
        return commandList;
    }
}
