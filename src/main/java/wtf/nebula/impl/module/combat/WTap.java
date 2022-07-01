package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet7UseEntity;
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

        if (event.getPacket() instanceof Packet7UseEntity) {

            Packet7UseEntity packet = event.getPacket();
            if (packet.isLeftClick == 1) {

                if (event.getEra().equals(Era.PRE)) {
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 4));
                }

                else {
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));
                }
            }
        }
    }
}
