package wtf.nebula.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import me.bush.eventbus.annotation.ListenerPriority;
import net.minecraft.src.Packet10Flying;
import net.minecraft.src.Packet11PlayerPosition;
import net.minecraft.src.Packet19EntityAction;
import net.minecraft.src.Packet7UseEntity;
import wtf.nebula.event.PacketEvent;
import wtf.nebula.event.PacketEvent.Era;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class Criticals extends Module {
    private static final double[][] CRITICALS = {
            { 0.1, 0.100000004768371, },
            { 0.119600000381469, 0.119600005149841 },
            { 0.060407999603271, 0.0604080043716424 }
    };

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.PACKET);
    public final Value<Integer> delay = new Value<>("Delay", 200, 0, 1000);
    public final Value<Boolean> stopSprint = new Value<>("StopSprint", false);
    public final Value<Integer> particles = new Value<>("Particles", 1, 0, 10);

    private double lastCrit = 0L;
    private boolean attacked = false;

    private int modifyStage = 0;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        lastCrit = 0L;
        attacked = false;
        modifyStage = 0;
    }

    @EventListener(priority = ListenerPriority.HIGHEST)
    public void onPacketSend(PacketEvent.Send event) {

        // if we use the entity
        if (event.getPacket() instanceof Packet7UseEntity) {

            // dont try to crit twice
            if (!event.getEra().equals(Era.PRE)) {
                return;
            }

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

                for (int i = 0; i < particles.getValue(); ++i) {
                    mc.thePlayer.onCriticalHit(mc.theWorld.getEntityByID(packet.targetEntity));
                }

                if (mode.getValue().equals(Mode.PACKET)) {

                    for (double[] offsets : CRITICALS) {
                        mc.thePlayer.sendQueue.addToSendQueue(new Packet11PlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.boundingBox.minY + offsets[0],
                                mc.thePlayer.posY + offsets[1],
                                mc.thePlayer.posZ,
                                false
                        ));
                    }

                    /*
                        2022-07-01 18:59:21 [CLIENT] [INFO] [CHAT] <Nebula> Stance: 5.720000004768371, Y: 4.1
                        2022-07-01 18:59:21 [CLIENT] [INFO] [CHAT] <Nebula> Stance: 5.739600005149841, Y: 4.119600000381469
                        2022-07-01 18:59:21 [CLIENT] [INFO] [CHAT] <Nebula> Stance: 5.6804080043716425, Y: 4.060407999603271
                     */
                }

                else if (mode.getValue().equals(Mode.MOTION)) {
                    mc.thePlayer.motionY = 0.1;
                    mc.thePlayer.fallDistance = 0.1f;
                    mc.thePlayer.onGround = false;
                }

                else if (mode.getValue().equals(Mode.MODIFY)) {
                    mc.thePlayer.onGround = false;
                    modifyStage = 0;
                    attacked = true;
                }
            }
        }

        else if (event.getPacket() instanceof Packet10Flying) {

            // our movement packet
            Packet10Flying packet = event.getPacket();

            if (attacked) {
                if (!mc.thePlayer.onGround) {
                    attacked = false;
                    return;
                }

                if (modifyStage >= CRITICALS.length) {
                    lastCrit = System.currentTimeMillis();
                    attacked = false;

                    packet.onGround = false;

                    return;
                }

                double[] offsets = CRITICALS[modifyStage];

                packet.yPosition += offsets[0];
                packet.stance += offsets[1];
                packet.onGround = false;

                ++modifyStage;
            }
        }

        else if (event.getPacket() instanceof Packet19EntityAction) {

            Packet19EntityAction packet = event.getPacket();

            if (packet.action == 5 && attacked && stopSprint.getValue()) {
                event.setCancelled(true);
            }
        }
    }

    public enum Mode {
        PACKET, MOTION, MODIFY
    }
}
