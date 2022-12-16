package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.network.play.client.C03PacketPlayer;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class NoFall extends ToggleableModule {
    private final Property<Boolean> reset = new Property<>(true, "Reset", "resetfalldist");
    private final Property<Float> distance = new Property<>(3.0f, 3.0f, 50.0f, "Distance", "dist", "falldist");

    public NoFall() {
        super("No Fall", new String[]{"nofall", "nofalldamage", "nofalldmg"}, ModuleCategory.MOVEMENT);
        offerProperties(reset, distance);
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (event.getPacket() instanceof C03PacketPlayer) {
            if (mc.thePlayer.fallDistance >= distance.getValue()) {
                ((C03PacketPlayer) event.getPacket()).onGround = true;

                if (reset.getValue()) {
                    mc.thePlayer.fallDistance = 0.0f;
                }
            }
        }
    }
}
