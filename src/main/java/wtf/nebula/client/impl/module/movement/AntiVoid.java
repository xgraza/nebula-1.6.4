package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AntiVoid extends ToggleableModule {
    private final Property<Boolean> autoDisable = new Property<>(true, "Auto Disable", "autodisable", "lagdisable");
    private final Property<Float> distance = new Property<>(10.0f, 3.0f, 40.0f, "Distance", "dist", "falldist");

    private boolean funny = false;

    public AntiVoid() {
        super("Anti Void", new String[]{"antivoid", "novoid"}, ModuleCategory.MOVEMENT);
        offerProperties(autoDisable, distance);
    }

    @Override
    public String getTag() {
        return String.valueOf(distance.getValue());
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        funny = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        if (mc.thePlayer.fallDistance >= distance.getValue() && !mc.isSingleplayer()) {
            funny = true;
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY + 2.0,
                    mc.thePlayer.boundingBox.minY + 2.0,
                    mc.thePlayer.posZ,
                    false));
            mc.thePlayer.fallDistance = 0.0f;
        }
    }

    @EventListener
    public void onPacketReceive(EventPacket event) {
        if (event.getEra().equals(Era.PRE) && event.getPacket() instanceof S08PacketPlayerPosLook && autoDisable.getValue() && funny) {
            setRunning(false);
        }
    }
}
