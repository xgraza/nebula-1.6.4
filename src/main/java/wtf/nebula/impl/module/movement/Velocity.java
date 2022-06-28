package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet28EntityVelocity;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {

        // if an entity has taken velocity and the server requests an entity takes kb
        if (event.getPacket() instanceof Packet28EntityVelocity) {

            // our velocity packet
            Packet28EntityVelocity packet = event.getPacket();

            // if the entity that this velocity needs to be applied to is the local player, cancel.
            if (packet.entityId == mc.thePlayer.entityId) {
                event.setCancelled(true);
            }
        }
    }
}
