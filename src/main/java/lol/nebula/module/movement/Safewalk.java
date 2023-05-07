package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.util.player.MoveUtils;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class Safewalk extends Module {
    public Safewalk() {
        super("Safe Walk", "Prevents you from falling off edges", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onMove(EventMove event) {
        if (mc.thePlayer.onGround && MoveUtils.isMoving()) MoveUtils.safewalk(event);
    }
}
