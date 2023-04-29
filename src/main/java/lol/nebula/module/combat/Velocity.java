package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

/**
 * @author aesthetical
 * @since 04/28/23
 */
public class Velocity extends Module {
    public Velocity() {
        super("Velocity", "Cancels velocity packets for no knockback", ModuleCategory.COMBAT);
    }

    @Listener
    public void onPacketInbound(EventPacket.Inbound event) {
        if (event.getPacket() instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity packet = event.getPacket();
            if (packet.func_149412_c() == mc.thePlayer.getEntityId()) {
                event.cancel();
            }
        } else if (event.getPacket() instanceof S27PacketExplosion) {
            event.cancel();
        }
    }
}
