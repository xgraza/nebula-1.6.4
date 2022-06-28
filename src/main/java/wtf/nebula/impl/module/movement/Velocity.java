package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet28EntityVelocity;
import net.minecraft.src.Packet60Explosion;
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

                packet.motionX = 0;
                packet.motionY = 0;
                packet.motionZ = 0;
            }
        }

        // if an explosion in the world happened
        else if (event.getPacket() instanceof Packet60Explosion) {

            // the explosion packet
            Packet60Explosion packet = event.getPacket();

            // decrease our velocity to 0
            packet.playerVelocityX = 0.0f;
            packet.playerVelocityY = 0.0f;
            packet.playerVelocityZ = 0.0f;
        }
    }
}
