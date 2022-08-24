package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0APacketAnimation;
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
        if (event.getPacket() instanceof C02PacketUseEntity) {
            if (!event.getEra().equals(Era.PRE)) {
                return;
            }

            C02PacketUseEntity packet = event.getPacket();

            if (!packet.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                return;
            }

            Entity entity = mc.theWorld.getEntityByID(packet.entityId);
            if (!(entity instanceof EntityBoat)) {
                return;
            }

            for (int i = 0; i < 10; ++i) {
                mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                mc.thePlayer.sendQueue.addToSendQueueSilent(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
            }
        }
    }
}
