package wtf.nebula.impl.module.misc;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class AutoRespawn extends Module {
    public AutoRespawn() {
        super("AutoRespawn", ModuleCategory.MISC);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            mc.thePlayer.respawnPlayer();
        }
    }
}
