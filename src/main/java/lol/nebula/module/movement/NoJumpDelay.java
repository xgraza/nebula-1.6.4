package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class NoJumpDelay extends Module {
    public NoJumpDelay() {
        super("No Jump Delay", "Removes the delay from jumping", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        mc.thePlayer.jumpTicks = 0;
    }
}
