package wtf.nebula.client.utils.player;

import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import wtf.nebula.client.utils.client.Wrapper;

public class MoveUtils implements Wrapper {
    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0.0f ||
                mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }

    public static double[] calcStrafe(double moveSpeed) {
        float forward = mc.thePlayer.movementInput.moveForward;
        float strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;

        if (forward != 0.0f) {
            if (strafe > 0.0f) {
                if (forward > 0.0f) {
                    yaw -= 45.0f;
                } else {
                    yaw += 45.0f;
                }
            } else if (strafe < 0.0f) {
                if (forward > 0.0f) {
                    yaw += 45.0f;
                } else {
                    yaw -= 45.0f;
                }
            }

            strafe = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }

        double sin = -Math.sin(Math.toRadians(yaw));
        double cos = Math.cos(Math.toRadians(yaw));

        return new double[] {
                forward * moveSpeed * sin + strafe * moveSpeed * cos,
                forward * moveSpeed * cos - strafe * moveSpeed * sin
        };
    }

    public static float getMovementYaw() {
        float rotationYaw = mc.thePlayer.rotationYaw;
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

    public static double getBaseNcpSpeed() {
        double base = 0.2873;

        if (mc.thePlayer == null) {
            return base;
        }

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            base *= 1.0 + (0.2 * (amp + 1));
        }

        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier();
            base /= 1.0 + (0.2 * (amp + 1));
        }

        return base;
    }

    public static double getJumpHeight(double base) {
        if (mc.thePlayer == null) {
            return base;
        }

        if (mc.thePlayer.isPotionActive(Potion.jump.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            base += (amp + 1.0) * 0.1;
        }

        return base;
    }

    public static double getJumpHeight(boolean low) {
        return getJumpHeight(low ? 0.3999 : 0.418);
//        double base = low ? 0.3999 : 0.418;
//
//        if (mc.thePlayer == null) {
//            return base;
//        }
//
//        if (mc.thePlayer.isPotionActive(Potion.jump.id)) {
//            int amp = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
//            base += (amp + 1.0) * 0.1;
//        }
//
//        return base;
    }
}
