package lol.nebula.util.player;

import lol.nebula.listener.events.entity.EventMove;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class MoveUtils {

    /**
     * The minecraft instance
     */
    protected static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * Checks if the player is moving
     * @return if the player is pressing WASD
     */
    public static boolean isMoving() {
        return mc.thePlayer.movementInput.moveForward != 0.0f
                || mc.thePlayer.movementInput.moveStrafe != 0.0f;
    }

    /**
     * Gets the base NCP speed
     * @param potionTime the minimum potion duration left before not factoring potions
     * @return the speed
     */
    public static double getBaseNcpSpeed(int potionTime) {
        double baseSpeed = 0.2873;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
            PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
            if (effect.getDuration() > potionTime) {
                int amp = effect.getAmplifier();
                baseSpeed *= 1.0 + (0.2 * (amp + 1));
            }
        }

        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown.id)) {
            PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown);
            if (effect.getDuration() > potionTime) {
                int amp = effect.getAmplifier();
                baseSpeed /= 1.0 + (0.2 * (amp + 1));
            }
        }

        return baseSpeed;
    }

    /**
     * Gets the jump height with potion modification
     * @return the jump height to use
     */
    public static double getJumpHeight(double base) {
        if (mc.thePlayer.isPotionActive(Potion.jump.id)) {
            int amp = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            base += (amp + 1.0) * 0.1;
        }

        return base;
    }

    /**
     * Strafes with movement
     * @param event the event to set the x and z motion
     * @param speed the speed to go at
     */
    public static void strafe(EventMove event, double speed) {
        if (speed == 0.0) {
            freeze(event);
            return;
        }

        float rad = getDirectionYaw();
        event.setX(-Math.sin(rad) * speed);
        event.setZ(Math.cos(rad) * speed);
    }

    /**
     * Freezes your x and z motion to 0.0
     * @param event the event to override
     */
    public static void freeze(EventMove event) {
        event.setX(0.0);
        event.setZ(0.0);
    }

    /**
     * Gets the player movement speed
     * @return the player movement speed
     */
    public static double getSpeed() {
        return Math.sqrt(
                mc.thePlayer.motionX * mc.thePlayer.motionX
                        + mc.thePlayer.motionX * mc.thePlayer.motionX
        );
    }

    /**
     * Gets the direction yaw for movement
     * @return the direction yaw for calculated strafe movement
     */
    public static float getDirectionYaw() {
        float rotationYaw = mc.thePlayer.rotationYaw;

        if (mc.thePlayer.moveForward < 0.0f) {
            rotationYaw += 180.0f;
        }

        float forward = 1.0f;
        if (mc.thePlayer.moveForward < 0.0f) {
            forward = -0.5f;
        } else if (mc.thePlayer.moveForward > 0.0f) {
            forward = 0.5f;
        }

        if (mc.thePlayer.moveStrafing > 0.0f) {
            rotationYaw -= 90.0f * forward;
        }

        if (mc.thePlayer.moveStrafing < 0.0f) {
            rotationYaw += 90.0f * forward;
        }

        return rotationYaw * 0.017453292f;
    }
}
