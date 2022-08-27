package wtf.nebula.client.impl.module.combat;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.network.PacketEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Criticals extends ToggleableModule {
    private static final double[][] CRITICALS = {
            { 0.1, 0.100000004768371, },
            { 0.119600000381469, 0.119600005149841 },
            { 0.060407999603271, 0.0604080043716424 }
    };

    private final Property<Mode> mode = new Property<>(Mode.PACKET, "Mode", "m", "type");

    public Criticals() {
        super("Criticals", new String[]{"crits"}, ModuleCategory.COMBAT);
        offerProperties(mode);
    }

    @EventListener
    public void onPacket(PacketEvent event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof C02PacketUseEntity) {
            C02PacketUseEntity packet = event.getPacket();

            if (!packet.getAction().equals(C02PacketUseEntity.Action.ATTACK)) {
                return;
            }

            if (!mc.thePlayer.onGround || mc.thePlayer.isInWater()) {
                return;
            }

            switch (mode.getValue()) {
                case PACKET: {
                    for (double[] offsets : CRITICALS) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                                mc.thePlayer.posX,
                                mc.thePlayer.boundingBox.minY + offsets[0],
                                mc.thePlayer.posY + offsets[1],
                                mc.thePlayer.posZ,
                                false
                        ));
                    }
                    break;
                }

                case MOTION: {
                    mc.thePlayer.motionY = 0.1;
                    break;
                }
            }
        }
    }

    public enum Mode {
        PACKET, MOTION
    }
}
