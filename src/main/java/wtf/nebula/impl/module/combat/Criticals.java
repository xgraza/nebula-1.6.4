package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet7UseEntity;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class Criticals extends Module {
    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
    }

    public final Value<Integer> delay = new Value<>("Delay", 200, 0, 1000);

    private double lastCrit = 0L;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lastCrit = 0L;
    }

    @EventListener
    public void onPacketSend(PacketEvent.Send event) {

        // if we use the entity
        if (event.getPacket() instanceof Packet7UseEntity) {
            Packet7UseEntity packet = event.getPacket();

            // if the packet is an attack packet (left click = attack)
            if (packet.isLeftClick == 1 && System.currentTimeMillis() - lastCrit >= delay.getValue()) {

                // dont crit if we're in water or on ground
                if (!mc.thePlayer.onGround || mc.thePlayer.isInWater()) {
                    return;
                }

                // reset timer
                lastCrit = System.currentTimeMillis();

                // stop sprinting
                if (mc.thePlayer.isSprinting()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));
                }

                // send movement packets to simulate a critical
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
