package wtf.nebula.util.world.player;

import net.minecraft.potion.Potion;
import wtf.nebula.util.Globals;

public class MotionUtil implements Globals {
    public static double[] strafe(double speed, float yaw) {
//        float[] movements = getMovement();
//
//        float forward = movements[0];
//        float strafe = movements[1];
//
//        double sin = -Math.sin(Math.toRadians(yaw));
//        double cos = Math.cos(Math.toRadians(yaw));
//
//        return new double[]{forward * speed * sin + strafe * speed * cos,
//                forward * speed * cos - strafe * speed * sin};

        float direction = getMovementYaw(yaw);

        double sin = -Math.sin(direction);
        double cos = Math.cos(direction);

        return new double[] { sin * speed, cos * speed };
    }

    public static double[] strafe(double speed) {
        return strafe(speed, mc.thePlayer.rotationYaw);
    }

    public static float getMovementYaw(float yaw) {
        float rotationYaw = yaw;
        float n = 1.0f;

        if (mc.thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        } else if (mc.thePlayer.movementInput.moveForward > 0.0f) {
            n = 0.5f;
        }

        if (mc.thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }

        if (mc.thePlayer.movementInput.moveStrafe < 0.0f) {
            rotationYaw += 90.0f * n;
        }

        return rotationYaw * 0.017453292f;
    }

//    public static float[] getMovement() {
//        float forward = mc.thePlayer.movementInput.moveForward;
//        float strafe = mc.thePlayer.movementInput.moveStrafe;
//        float yaw = mc.thePlayer.rotationYaw;
//
//        if (forward != 0.0f) {
//            if (strafe > 0.0f) {
//                yaw += forward > 0.0f ? -45.0f : 45.0f;
//            } else if (strafe < 0.0f) {
//                yaw += forward > 0.0f ? 45.0f : -45.0f;
//            }
//
//            strafe = 0.0f;
//            if (forward > 0.0f) {
//                forward = 1.0f;
//            } else if (forward < 0.0f) {
//                forward = -1.0f;
//            }
//        }
//
//        return new float[]{forward, strafe, yaw};
//    }

    public static boolean isMoving() {
        return mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f;
    }

    public static double getBaseNcpSpeed() {
        if (mc.thePlayer == null) {
            return 0.0;
        }

        double baseSpeed = 0.2873;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }

        return baseSpeed;
    }

    public static double getJumpHeight() {
        if (mc.thePlayer == null) {
            return 0.0;
        }

        double height = 0.3995;

        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            height += (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1;
        }

        return height;
    }
}
