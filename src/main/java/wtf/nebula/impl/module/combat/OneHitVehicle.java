package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Entity;
import net.minecraft.src.EntityBoat;
import net.minecraft.src.Packet18Animation;
import net.minecraft.src.Packet7UseEntity;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class OneHitVehicle extends Module {
    public OneHitVehicle() {
        super("1HitVehicle", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof Packet7UseEntity) {
            if (!event.getEra().equals(Era.PRE)) {
                return;
            }

            Packet7UseEntity packet = event.getPacket();

            if (packet.isLeftClick == 0) {
                return;
            }

            Entity entity = mc.theWorld.getEntityByID(packet.targetEntity);
            if (!(entity instanceof EntityBoat)) {
                return;
            }

            for (int i = 0; i < 10; ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new Packet18Animation());
                mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet7UseEntity(mc.thePlayer.entityId, packet.targetEntity, 1));
            }
        }
    }
}
