package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class VehicleOneTap extends Module {
    public VehicleOneTap() {
        super("Vehicle One Tap", "One taps vehicles", ModuleCategory.COMBAT);
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();

            // if we did not attack this vehicle or it's not a vehicle entity at all
            if (packet.getAction() != Action.ATTACK || !isVehicle(packet.getEntity(mc.theWorld))) return;

            for (int i = 0; i < 10; ++i) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(
                        new C02PacketUseEntity(packet.getEntity(mc.theWorld), Action.ATTACK));
            }
        }
    }

    private boolean isVehicle(Entity entity) {
        return entity instanceof EntityBoat || entity instanceof EntityMinecart;
    }

}
