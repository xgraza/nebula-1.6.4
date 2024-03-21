package nebula.client.rotate;

import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.EventStage;
import nebula.client.listener.event.player.EventMoveUpdate;
import nebula.client.listener.event.player.rotate.EventHeadRotations;
import nebula.client.listener.event.player.rotate.EventRotateBody;
import nebula.client.util.chat.Printer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

import static nebula.client.util.math.AngleUtils.validate;

/**
 * @author Gavin
 * @since 08/18/23
 */
public class RotationSpoofer {

  /**
   * The minecraft game instance
   */
  private final Minecraft mc = Minecraft.getMinecraft();

  /**
   * The currently spoofed rotation
   */
  private Rotation spoofed;
  private int keepTicks;

  public RotationSpoofer() {
    Nebula.BUS.subscribe(this);
  }

  @Subscribe
  private final Listener<EventMoveUpdate> moveUpdate = event -> {

    if (event.stage() == EventStage.PRE && spoofed != null) {

      float[] angles = spoofed.angles();
      event.setYaw(angles[0]);
      event.setPitch(angles[1]);

      --keepTicks;
      if (keepTicks <= 0) {
        spoofed = null;
        keepTicks = 0;
        return;
      }

      mc.thePlayer.rotationYawHead = angles[0];
      mc.thePlayer.renderPitch = angles[1];
    }
  };

  @Subscribe
  private final Listener<EventRotateBody> rotateBody = event -> {
    if (spoofed != null && event.entity().equals(mc.thePlayer)) {
      event.setCanceled(true);
      correctBodyRotations(spoofed.angles());
    }
  };

  @Subscribe
  private final Listener<EventHeadRotations> headRotations = event -> {
    if (spoofed != null && event.entity().equals(mc.thePlayer)) {
      event.setCanceled(true);
    }
  };

  private void correctBodyRotations(float[] angles) {
    double diffX = mc.thePlayer.posX - mc.thePlayer.prevPosX;
    double diffZ = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
    float distance = (float) (diffX * diffX + diffZ * diffZ);

    float yawOffset = mc.thePlayer.renderYawOffset;

    if (distance > 0.0025000002f) {
      yawOffset = (float) Math.atan2(diffZ, diffX) * 180.0f / (float) Math.PI - 90.0f;
    }

    if (mc.thePlayer.swingProgress > 0.0f) {
      yawOffset = angles[0];
    }

    float diff = MathHelper.wrapAngleTo180_float(yawOffset - mc.thePlayer.renderYawOffset);
    mc.thePlayer.renderYawOffset += diff * 0.3f;
    float yawDiff = MathHelper.wrapAngleTo180_float(angles[0] - mc.thePlayer.renderYawOffset);

    if (yawDiff < -75.0f) {
      yawDiff = -75.0f;
    }

    if (yawDiff >= 75.0f) {
      yawDiff = 75.0f;
    }

    mc.thePlayer.renderYawOffset = angles[0] - yawDiff;
    if (yawDiff * yawDiff > 2500.0f) {
      mc.thePlayer.renderYawOffset += yawDiff * 0.2f;
    }
  }

  /**
   * Resets the spoofed rotations
   * @param priority the priority
   */
  public void reset(int priority) {
    if (spoofed != null && spoofed.priority() <= priority) {
      spoofed = null;
      keepTicks = 0;
    }
  }

  /**
   * Submits a rotation to be spoofed to the server
   * @param rotation the rotation object to spoof
   */
  public void submit(Rotation rotation) {
    if (spoofed != null
      && spoofed.priority() > rotation.priority()) return;

    if (!validate(rotation.angles(), true)) return;

    spoofed = rotation;
    keepTicks = rotation.ticks();
  }

  /**
   * Submits a rotation to be spoofed to the server with no pre checks
   * @param rotation the rotation object to spoof
   */
  public void submitForced(Rotation rotation) {
    spoofed = rotation;
    keepTicks = rotation.ticks();
  }
}
