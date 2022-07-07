package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet3Chat;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.irc.IRCServer;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class IRC extends Module {
    public static IRCServer server;

    public IRC() {
        super("IRC", ModuleCategory.MISC);
        drawn.setValue(false);
        setState(true);
    }

    @Override
    protected void onActivated() {
        super.onActivated();
        server = new IRCServer();
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        server.disconnect();
        server = null;
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet3Chat && event.getEra().equals(Era.PRE)) {
            String message = ((Packet3Chat) event.getPacket()).message;
            if (message.startsWith("@")) {
                event.setCancelled(true);

                String parsed = message.substring(1);

                String[] args = parsed.split(" ");
                if (args.length > 0 && args[0].equalsIgnoreCase("reconnect")) {
                    server.disconnect();
                    server = null;
                    server = new IRCServer();

                    return;
                }

                server.sendIRCChatMessage(parsed);
            }
        }
    }
}
