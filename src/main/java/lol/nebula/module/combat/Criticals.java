package lol.nebula.module.combat;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.net.EventPacket;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.math.timing.Timer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.potion.Potion;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Criticals extends Module {

    private final Setting<Mode> mode = new Setting<>(Mode.PACKET, "Mode");
    private final Setting<Double> delay = new Setting<>(0.5, 0.01, 0.0, 5.0, "Delay");
    private final Setting<Integer> hurtTime = new Setting<>(3, 0, 20, "Hurt Time");

    private final Timer timer = new Timer();

    public Criticals() {
        super("Criticals", "Makes your attacks critical hits", ModuleCategory.COMBAT);
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();

            // if we didnt attack or the entity is not a living entity, dont continue
            if (packet.getAction() != Action.ATTACK || !(packet.getEntity(mc.theWorld) instanceof EntityLivingBase)) return;

            // if we are not on ground, or in water, or we have blindness
            if (!mc.thePlayer.onGround
                    || mc.thePlayer.isInWater()
                    || mc.thePlayer.isInWeb
                    || mc.thePlayer.isPotionActive(Potion.blindness)) return;

            // if we have not passed the delay, do not continue
            if (!timer.ms((long) (delay.getValue() * 1000.0), false)) return;

            // if we are below our hurt time requirement, don't crit
            if (((EntityLivingBase) packet.getEntity(mc.theWorld)).hurtTime > hurtTime.getValue()) return;

            switch (mode.getValue()) {
                case PACKET:

                    // i just packet logged stance and y pos for 0.1 motion y
                    mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.boundingBox.minY + 0.1, mc.thePlayer.posY + 0.100000004768371,
                            mc.thePlayer.posZ, false));
                    mc.thePlayer.sendQueue.addToSendQueue(new C04PacketPlayerPosition(
                            mc.thePlayer.posX,
                            mc.thePlayer.boundingBox.minY, mc.thePlayer.posY,
                            mc.thePlayer.posZ, false));
                    break;

                case MOTION:
                    mc.thePlayer.motionY = 0.11;
                    break;
            }
        }
    }

    public enum Mode {
        PACKET, MOTION
    }
}
