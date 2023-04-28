package lol.nebula.util.player;

import lol.nebula.listener.events.entity.EventMove;
import net.minecraft.client.Minecraft;

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
