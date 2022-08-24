package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onPacketReceive(PacketEvent.Receive event) {

        // nullcheck
        if (nullCheck()) {
            return;
        }

        // if an entity has taken velocity and the server requests an entity takes kb
        if (event.getPacket() instanceof S12PacketEntityVelocity) {

            // our velocity packet
            S12PacketEntityVelocity packet = event.getPacket();

            // if the entity that this velocity needs to be applied to is the local player, cancel.
            if (packet.entityId == mc.thePlayer.getEntityId()) {
                event.setCancelled(true);
            }
        }

        // if an explosion in the world happened
        else if (event.getPacket() instanceof S27PacketExplosion) {

            // the explosion packet
            S27PacketExplosion packet = event.getPacket();

            event.setCancelled(true);

//            // decrease our velocity to 0
//            packet.playerVelocityX = 0.0f;
//            packet.playerVelocityY = 0.0f;
//            packet.playerVelocityZ = 0.0f;
        }
    }
}
