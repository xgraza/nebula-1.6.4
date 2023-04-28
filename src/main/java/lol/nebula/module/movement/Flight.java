package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventMove;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.player.MoveUtils;

import static org.lwjgl.input.Keyboard.KEY_G;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Flight extends Module {

    private final Setting<Double> speed = new Setting<>(0.5, 0.01, 0.01, 10.0, "Speed");
    private final Setting<Boolean> viewBob = new Setting<>(true, "View Bobbing");
    private final Setting<Boolean> antiKick = new Setting<>(false, "Anti-Kick");

    public Flight() {
        super("Flight", "Allows you to fly in survival mode", ModuleCategory.MOVEMENT);

        getBind().setKey(KEY_G);
    }

    @Listener
    public void onMove(EventMove event) {
        if (MoveUtils.isMoving()) {
            MoveUtils.strafe(event, speed.getValue());
        } else {
            MoveUtils.freeze(event);
        }

        double motionY = 0.0;
        if (mc.gameSettings.keyBindJump.pressed) {
            motionY = speed.getValue();
        } else if (mc.gameSettings.keyBindSneak.pressed) {
            motionY = -speed.getValue();
        } else {
            if (antiKick.getValue()) {
                motionY = -0.3126;
            }
        }

        event.setY(motionY);
        mc.thePlayer.motionY = motionY;
    }
}
