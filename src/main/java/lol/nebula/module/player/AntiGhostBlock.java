package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.EnumFacing;

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

            int x = packet.func_149576_c();
            int y = packet.func_149571_d();
            int z = packet.func_149570_e();

            // if the packet is a use packet (aka BlockPos is -1, return
            if (x == -1 && y == -1 && z == -1) return;

            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(
                    x, y - 1, z, EnumFacing.UP.getOrder_a(), null, 0.0f, 0.5f, 0.0f));
        }
    }
}
