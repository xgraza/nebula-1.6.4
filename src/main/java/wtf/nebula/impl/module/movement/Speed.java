package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.AxisAlignedBB;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.MotionUpdateEvent;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;
import wtf.nebula.util.world.MotionUtil;

import java.util.List;

import static wtf.nebula.util.world.MotionUtil.*;

public class Speed extends Module {
    public Speed() {
        super("Speed", ModuleCategory.MOVEMENT);
        setBind(Keyboard.KEY_R);
    }

    public final Value<Mode> mode = new Value<>("Mode", Mode.STRAFE);

    private double traveled = 0.0;
    private double moveSpeed = 0.0;
    private int stage = 0;
    private boolean slow = false;

    @Override
    protected void onDeactivated() {
        super.onDeactivated();

        traveled = 0.0;
        moveSpeed = 0.0;
        stage = 0;
    }

    @EventListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        double x = mc.thePlayer.posX - mc.thePlayer.prevPosX;
        double z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;

        traveled = Math.sqrt(x * x + z * z);
    }

    @EventListener
    public void onTick(TickEvent event) {

        if (mode.getValue().equals(Mode.STRAFE)) {
            if (mc.thePlayer.onGround && isMoving()) {
                stage = 2;
            }

            if (stage == 1) {
                stage = 2;
                moveSpeed = 1.38 * getBaseNcpSpeed() - 0.01;
            } else if (stage == 2) {
                stage = 3;

                if (mc.thePlayer.onGround && isMoving()) {
                    mc.thePlayer.motionY = getJumpHeight();
                    moveSpeed *= 2.149;
                }
            } else if (stage == 3) {
                stage = 4;

                double adjusted = 0.66 * (traveled - getBaseNcpSpeed());
                moveSpeed = traveled - adjusted;

                slow = !slow;
            } else {

                List<AxisAlignedBB> boxes = mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.boundingBox.offset(0.0, mc.thePlayer.motionY, 0.0));
                if (!boxes.isEmpty() && mc.thePlayer.isCollidedVertically) {
                    stage = 1;
                }

                moveSpeed = moveSpeed - moveSpeed / 159.0;
            }

            moveSpeed = Math.max(moveSpeed, getBaseNcpSpeed());

            double[] strafe = MotionUtil.strafe(moveSpeed);

            mc.thePlayer.motionX = strafe[0];
            mc.thePlayer.motionZ = strafe[1];
        }
    }

    public enum Mode {
        STRAFE, ONGROUND, YPORT
    }
}
