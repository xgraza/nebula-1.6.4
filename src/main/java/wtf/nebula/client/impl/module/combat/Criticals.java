package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;

public class Criticals extends ToggleableModule {
    private final Property<Mode> mode = new Property<>(Mode.PACKET, "Mode", "m", "type");
    private final Property<Double> delay = new Property<>(500.0, 0.0, 2000.0, "Delay", "d");
    private final Property<Boolean> stopSprint = new Property<>(true, "Stop Sprint", "alwayscrit");

    private final Timer timer = new Timer();
    private int modifyStage = -1;

    public Criticals() {
        super("Criticals", new String[]{"crits"}, ModuleCategory.COMBAT);
        offerProperties(mode, delay, stopSprint);
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        modifyStage = -1;
    }

    @Override
    public String getTag() {
        return mode.getFixedValue();
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getEra().equals(Era.PRE)) {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = event.getPacket();

                if (!packet.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                    return;
                }

                if (!mc.thePlayer.onGround || mc.thePlayer.isInWater()) {
                    return;
                }

                Entity entity = mc.theWorld.getEntityByID(packet.entityId);
                if (!(entity instanceof EntityLivingBase)) {
                    return;
                }

                if (!timer.hasPassed(delay.getValue().longValue(), true)) {
                    return;
                }

                boolean noSprint = stopSprint.getValue() && mc.thePlayer.isSprinting();
                if (noSprint) {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, 5));
                }

                switch (mode.getValue()) {
                    case PACKET: {
                        mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.boundingBox.minY + 0.1,
                                mc.thePlayer.posY + 0.100000004768371,
                                mc.thePlayer.posZ,
                                false
                        ));
                        mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.boundingBox.minY,
                                mc.thePlayer.posY,
                                mc.thePlayer.posZ,
                                false
                        ));
                        break;
                    }

                    case MOTION: {
                        mc.thePlayer.motionY = 0.1;
                        break;
                    }

                    case MODIFY: {
                        modifyStage = 0;
                        break;
                    }
                }

                if (noSprint) {
                    mc.thePlayer.sendQueue.addToSendQueueSilent(new C0BPacketEntityAction(mc.thePlayer, 4));
                }
            } else if (event.getPacket() instanceof C03PacketPlayer) {
                C03PacketPlayer packet = event.getPacket();
                if (mode.getValue().equals(Mode.MODIFY) && modifyStage != -1) {

                    if (modifyStage == 0) {
                        packet.onGround = false;
                        packet.y += 0.1;
                        packet.stance += 0.100000004768371;
                        modifyStage = 1;
                    } else if (modifyStage == 1) {
                        packet.onGround = false;
                        modifyStage = -1;
                    }
                }
            }
        }
    }

    public enum Mode {
        PACKET, MODIFY, MOTION
    }
}
