package nebula.client.util.math;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

/**
 * @author Gavin
 * @since 08/17/23
 */
public class AngleUtils {

  private static final Minecraft mc = Minecraft.getMinecraft();

  /**
   * Calculates rotations to a block
   * @param pos the position
   * @param facing the block face
   * @return the angles
   */
  public static float[] block(Vec3 pos, EnumFacing facing) {

    Vec3 eyes = mc.thePlayer.getPosition(1.0f);

    int offsetX = facing.getFrontOffsetX();
    int offsetY = facing.getFrontOffsetY();
    int offsetZ = facing.getFrontOffsetZ();

    double deltaX = (Math.floor(eyes.xCoord) + 0.5)
      - (Math.floor(pos.xCoord) + 0.5 - (offsetX * 0.5));
    double deltaZ = (Math.floor(eyes.zCoord) + 0.5)
      - (Math.floor(pos.zCoord) + 0.5 - (offsetZ * 0.5));

    double dist = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

    float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) + 90.0f);
    float pitch = (float) Math.toDegrees(Math.atan2(
      eyes.yCoord - (pos.yCoord - (offsetY * 0.5)), dist));

    return new float[] { yaw, pitch };
  }

  /**
   * Validates an array of angles
   * @param angles the angles
   * @param pitchCheck if to check if the pitch is in bounds
   * @return if the angles are able to be spoofed to the server
   */
  public static boolean validate(float[] angles, boolean pitchCheck) {
    if (angles == null || angles.length != 2) return false;

    if (Float.isNaN(angles[0])
      || Float.isNaN(angles[1])) return false;

    return !pitchCheck
      || angles[1] <= 90.0f && angles[1] >= -90.0f;
  }

  /**
   * Calculates a fluent angle rotation
   * @param angle the angle
   * @return the resulted angle
   */
  public static float calculateRotation(float angle) {
    if ((angle %= 360.0F) >= 180.0F) {
      angle -= 360.0F;
    }

    if (angle < -180.0F) {
      angle += 360.0F;
    }

    return angle;
  }

}
