package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventMove;
import lol.nebula.listener.events.entity.EventWalkingUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.player.MoveUtils;

import java.util.List;

/**
 * @author aesthetical
 * @since 04/30/23
 */
public class LongJump extends Module {

    private final Setting<Double> boost = new Setting<>(4.5, 0.1, 1.0, 8.0, "Boost");
    private final Setting<Boolean> glide = new Setting<>(true, "Glide");

    private double speed, distance;
    private int stage;

    public LongJump() {
        super("Long Jump", "Makes you jump longer", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        stage = 1;
        distance = 0.0;
        speed = 0.0;
    }

    @Listener
    public void onMove(EventMove event) {
        if (MoveUtils.isMoving()) {
            if (stage == 1) {
                speed = boost.getValue() * 0.2873 - 0.05;
                stage = 2;
            } else if (stage == 2) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.motionY = MoveUtils.getJumpHeight(0.42);
                    event.setY(mc.thePlayer.motionY);
                }

                speed *= 2.149;
                stage = 3;
            } else if (stage == 3) {
                double adjust = 0.66 * (distance - MoveUtils.getBaseNcpSpeed(0));
                speed = distance - adjust;
                stage = 4;
            } else {
                speed -= speed / 150.0;
                if (mc.thePlayer.onGround) {
                    stage = 1;
                }
            }

            // credits to Doogie13 for this block of code
            if (glide.getValue()) {
                List list1 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                        mc.thePlayer.boundingBox.copy().offset(0.0, mc.thePlayer.motionY, 0.0));
                List list2 = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer,
                        mc.thePlayer.boundingBox.copy().offset(0.0, -0.4, 0.0));

                if (!mc.thePlayer.isCollidedVertically && (!list1.isEmpty() || !list2.isEmpty())) {
                    mc.thePlayer.motionY = -1.0E-4;
                    event.setY(mc.thePlayer.motionY);
                }
            }
        }

        MoveUtils.strafe(event, speed);
    }

    @Listener
    public void onWalkingUpdate(EventWalkingUpdate event) {

        // calculate move speed
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
        distance = Math.sqrt(x * x + z * z);
    }
}
