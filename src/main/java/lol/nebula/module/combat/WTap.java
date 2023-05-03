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
import net.minecraft.network.play.client.C0BPacketEntityAction;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class WTap extends Module {

    private final Setting<Double> delay = new Setting<>(1.0, 0.01, 0.0, 10.0, "Delay");

    private final Timer timer = new Timer();

    public WTap() {
        super("W-Tap", "Makes entities take more knockback from your hits", ModuleCategory.COMBAT);
    }

    @Listener
    public void onPacketOutbound(EventPacket.Outbound event) {
        if (event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();
            if (packet.getAction() != Action.ATTACK
                    || !(packet.getEntity(mc.theWorld) instanceof EntityLivingBase)) return;

            if (!timer.ms((long) (delay.getValue() * 1000.0), false)) return;

            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0BPacketEntityAction(mc.thePlayer, 4));
            mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0BPacketEntityAction(mc.thePlayer, 5));

            if (mc.thePlayer.isSprinting()) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C0BPacketEntityAction(mc.thePlayer, 4));
            }
        }
    }
}
