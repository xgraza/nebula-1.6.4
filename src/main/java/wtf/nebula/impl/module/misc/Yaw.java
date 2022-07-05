package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Yaw extends Module {
    public Yaw() {
        super("Yaw", ModuleCategory.MISC);
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.thePlayer.rotationYaw = Math.round((mc.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
    }
}
