package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class Sprint extends Module {

    private final Setting<Mode> mode = new Setting<>(Mode.VANILLA, "Mode");

    public Sprint() {
        super("Sprint", "Automatically sprints for you", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.thePlayer != null && !mc.gameSettings.keyBindSprint.pressed) {
            mc.thePlayer.setSprinting(false);
        }
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (mc.thePlayer.isSprinting()) return;

        if (mode.getValue() == Mode.VANILLA) {
            mc.thePlayer.setSprinting(
                    mc.thePlayer.movementInput.moveForward > 0.0f
                    && !mc.thePlayer.isCollidedHorizontally
                    && mc.thePlayer.getFoodStats().getFoodLevel() > 6
                    && !mc.thePlayer.isSprinting());
        } else if (mode.getValue() == Mode.RAGE) {
            mc.thePlayer.setSprinting(true);
        }
    }

    public enum Mode {
        VANILLA, RAGE
    }
}
