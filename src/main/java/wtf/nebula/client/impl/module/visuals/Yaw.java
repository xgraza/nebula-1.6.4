package wtf.nebula.client.impl.module.visuals;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class Yaw extends ToggleableModule {
    public Yaw() {
        super("Yaw", new String[]{"yaw", "yawlock"}, ModuleCategory.VISUALS);
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.thePlayer.rotationYaw = Math.round((mc.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
    }
}
