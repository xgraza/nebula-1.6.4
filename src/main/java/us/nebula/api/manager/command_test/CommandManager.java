package us.nebula.api.manager.command_test;

import net.minecraft.network.play.client.C01PacketChatMessage;
import us.nebula.Nebula;
import us.nebula.api.listener.EventBus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.IManager;
import us.nebula.impl.command_test.GCCommand;
import us.nebula.impl.command_test.HelpCommand;
import us.nebula.impl.command_test.SpawnTPCommand;
import us.nebula.impl.event.network.EventPacket;
import us.nebula.util.player.ChatUtil;
import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.parser.CommandContext;
import world.xgraza.xcmd.registry.CommandRegistry;

/**
 * @author xgraza
 * @since 08/12/25
 */
public final class CommandManager extends CommandRegistry implements IManager
{
    private static final String COMMAND_PREFIX = ".";

    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C01PacketChatMessage)
        {
            final C01PacketChatMessage packet = event.getPacket();
            final String message = packet.getMessage();
            if (message.startsWith(COMMAND_PREFIX))
            {
                event.cancel();
                process(message);
            }
        }
    };

    public CommandManager()
    {
        super(COMMAND_PREFIX);
    }

    @Override
    public void init()
    {
        EventBus.subscribe(this);
        register(new GCCommand());
        register(new HelpCommand(this));
        register(new SpawnTPCommand());
    }

    @Override
    public void handleDispatchResult(final CommandResult commandResult, final CommandContext commandContext)
    {
        ChatUtil.send(commandResult.getMessage());
    }

    @Override
    public void handleDispatchException(final Exception e)
    {
        ChatUtil.send("&cException Occurred -> {}", e.getMessage());
        Nebula.INSTANCE.getLogger().error(e);
    }
}
