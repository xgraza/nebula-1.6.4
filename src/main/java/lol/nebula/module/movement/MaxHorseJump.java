package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class MaxHorseJump extends Module {
    public MaxHorseJump() {
        super("Max Horse Jump", "Makes horse jumps automatically full", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (!mc.thePlayer.isRidingHorse()) return;

        mc.thePlayer.horseJumpPower = 1.0f;
    }
}
