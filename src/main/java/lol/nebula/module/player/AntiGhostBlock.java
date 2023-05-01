package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

/**
 * @author aesthetical
 * @since 05/01/23
 */
public class AntiGhostBlock extends Module {
    public AntiGhostBlock() {
        super("Anti Ghost Block", "Stops ghost blocks from spawning", ModuleCategory.PLAYER);
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C08PacketPlayerBlockPlacement) {
            C08PacketPlayerBlockPlacement packet = event.getPacket();

            // todo
        }
    }
}
