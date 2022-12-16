package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.impl.event.impl.move.EventMotion;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.player.MoveUtils;

public class Static extends ToggleableModule {
    public Static() {
        super("Static", new String[]{"stopmotion"}, ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onMotion(EventMotion event) {
        if (!MoveUtils.isMoving()) {
            event.x = 0.0;
            event.z = 0.0;
            mc.thePlayer.motionX = 0.0;
            mc.thePlayer.motionZ = 0.0;
        }
    }
}
