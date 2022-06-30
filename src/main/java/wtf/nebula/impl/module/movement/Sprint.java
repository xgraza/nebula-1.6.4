package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Sprint extends Module {
    public Sprint() {
        super("Sprint", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDeactivated() {

        // reset sprint state
        mc.thePlayer.setSprinting(false);
    }

    @EventListener
    public void onTick(TickEvent event) {

        if (!mc.thePlayer.isSprinting()) {
            mc.thePlayer.setSprinting(mc.thePlayer.movementInput.moveForward > 0.0f
                    && !mc.thePlayer.isSneaking()
                    && !mc.thePlayer.isUsingItem()
                    && mc.thePlayer.getFoodStats().getFoodLevel() > 6);
        }
    }
}
