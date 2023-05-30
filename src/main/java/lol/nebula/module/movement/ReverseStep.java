package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.move.EventMove;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author aesthetical
 * @since 05/30/23
 */
public class ReverseStep extends Module {

    private final Setting<Double> distance = new Setting<>(2.0, 0.5, 0.5, 50.0, "Distance");
    private final Setting<Double> speed = new Setting<>(1.0, 0.01, 0.1, 20.0, "Downforce");

    public ReverseStep() {
        super("Reverse Step", "gabagoo", ModuleCategory.MOVEMENT);
    }

    @Listener
    public void onMove(EventMove event) {
        if (!mc.thePlayer.onGround
                || mc.thePlayer.isOnLadder()
                || mc.thePlayer.isInWater()
                || Jesus.isAboveWater()
                || mc.gameSettings.keyBindJump.pressed) return;

        for (double offset = 0.0; offset < distance.getValue() + 0.5; offset += 0.01) {
            AxisAlignedBB bb = mc.thePlayer.boundingBox.copy().offset(
                    0.0, -offset, 0.0);
            if (!mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && bb.minY > 0.0) {
                event.setY(-speed.getValue());
                mc.thePlayer.motionY = event.getY();
                break;
            }
        }
    }
}
