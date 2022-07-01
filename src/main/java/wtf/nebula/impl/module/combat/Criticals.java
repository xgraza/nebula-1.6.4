package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.src.Packet10Flying;
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

    public final Value<Mode> mode = new Value<>("Mode", Mode.PACKET);
    public final Value<Integer> delay = new Value<>("Delay", 200, 0, 1000);
    public final Value<Boolean> stopSprint = new Value<>("StopSprint", false);

    private double lastCrit = 0L;
    private boolean attacked = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lastCrit = 0L;
        attacked = false;
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
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
                if (mc.thePlayer.isSprinting() && stopSprint.getValue()) {
                    mc.thePlayer.sendQueue.addToSendQueue(new Packet19EntityAction(mc.thePlayer, 5));
                }

                if (mode.getValue().equals(Mode.PACKET)) {

                    mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.boundingBox.minY + 0.12,
                            mc.thePlayer.posY + 0.12,
                            mc.thePlayer.posZ,
                            false
                    ));

                    mc.thePlayer.sendQueue.addToSendQueueSilent(new Packet11PlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.boundingBox.minY,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            false
                    ));
                }

                else if (mode.getValue().equals(Mode.MOTION)) {
                    mc.thePlayer.motionY = 0.1;
                    mc.thePlayer.fallDistance = 0.1f;
                    mc.thePlayer.onGround = false;
                }
            }
        }

//        else if (event.getPacket() instanceof Packet10Flying) {
//
//            Packet10Flying packet = event.getPacket();
//
//            if (attacked) {
//                attacked = false;
//
//                packet.onGround = false;
//                packet.yPosition += 0.10000000149011612;
//                packet.stance += 0.10000000149011612;
//
//                mc.thePlayer.fallDistance = 0.1f;
//            }
//        }
    }

    public enum Mode {
        PACKET, MOTION
    }
}
