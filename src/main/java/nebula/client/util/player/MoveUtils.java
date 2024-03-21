package nebula.client.util.player;

import nebula.client.listener.event.player.EventMove;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * @author Gavin
 * @since 08/23/23
 */
public class MoveUtils {

  /**
   * The minecraft game instance
   */
  private static final Minecraft mc = Minecraft.getMinecraft();

  private static final double[] ZERO = { 0, 0 };

  /**
   * Checks if the local player is moving
   * @return if the local player is moving
   */
  public static boolean moving() {
    return (
      mc.thePlayer.movementInput.moveForward != 0.0f ||
        mc.thePlayer.movementInput.moveStrafe != 0.0f);
  }

  public static double[] strafe(double speed) {
    if (speed <= 0.0) return ZERO;

    float x = strafeDirection();
    return new double[] {
      speed * -Math.sin(x), speed * Math.cos(x) };
  }

  public static void strafe() {
    double[] strafe = strafe(speed());
    mc.thePlayer.motionX = strafe[0];
    mc.thePlayer.motionZ = strafe[1];
  }

  public static void strafe(EventMove event, double speed) {
    double[] xz = strafe(speed);
    event.setX(xz[0]);
    event.setZ(xz[1]);

    mc.thePlayer.motionX = xz[0];
    mc.thePlayer.motionZ = xz[1];
  }

  public static double ncpBaseSpeed() {
    double speed = 0.2873;
    if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
      PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed);
      speed *= 1.0 + (0.2 * (effect.getAmplifier() + 1));
    }
    return speed;
  }

  public static double speed() {
    return Math.sqrt(
      mc.thePlayer.motionX * mc.thePlayer.motionX +
        mc.thePlayer.motionZ * mc.thePlayer.motionZ);
  }

  public static float jumpHeight(float base) {

    if (mc.thePlayer.isPotionActive(Potion.jump)) {
      PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.jump);
      base += (effect.getAmplifier() + 1) * 0.1f;
    }
    return base;
  }

  public static float strafeDirection() {
    // get our current angle (our player yaw rotation)
    float yaw = mc.thePlayer.rotationYaw;

    // if we're moving backwards, reverse our yaw
    if (mc.thePlayer.moveForward < 0.0f) yaw -= 180.0f;

    // this is for handling holding forward & strafing side to side at the same time
    float forward = mc.thePlayer.moveForward * 0.5f;
    if (forward == 0.0f) forward = 1.0f;

    float strafe = mc.thePlayer.moveStrafing;
    if (strafe > 0.0f) {
      yaw -= 90.0f * forward;
    } else if (strafe < 0.0f) {
      yaw += 90.0f * forward;
    }

    // convert angle to radians
    return (float) Math.toRadians(yaw);
  }
}
