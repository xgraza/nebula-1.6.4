package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet7UseEntity;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        setBind(Keyboard.KEY_C);
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // if we use the entity
        if (event.getPacket() instanceof Packet7UseEntity) {
            Packet7UseEntity packet = event.getPacket();

            // if the packet is an attack packet (left click = attack)
            if (packet.isLeftClick == 1) {

                // send movement packets to simulate an attack
                mc.thePlayer.sendQueue.addToSendQueue(new Packet11PlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.boundingBox.minY + 0.2,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        false
                ));

                mc.thePlayer.sendQueue.addToSendQueue(new Packet11PlayerPosition(
                        mc.thePlayer.posX,
                        mc.thePlayer.boundingBox.minY,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        false
                ));
            }
        }
    }
}
