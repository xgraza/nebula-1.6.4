package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class WTap extends Module {
    public WTap() {
        super("WTap", ModuleCategory.COMBAT);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        if (event.getPacket() instanceof C02PacketUseEntity) {

            C02PacketUseEntity packet = event.getPacket();
            if (packet.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {

                if (event.getEra().equals(Era.PRE)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 4));
                }

                else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, 5));
                }
            }
        }
    }
}
