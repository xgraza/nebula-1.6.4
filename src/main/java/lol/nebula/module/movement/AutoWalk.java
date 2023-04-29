package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class AutoWalk extends Module {
    public AutoWalk() {
        super("Auto Walk", "Automatically walks for you", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // set pressed to false
        if (mc.gameSettings != null) mc.gameSettings.keyBindForward.pressed = false;
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        mc.gameSettings.keyBindForward.pressed = true;
    }
}
