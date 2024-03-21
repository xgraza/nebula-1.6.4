package nebula.client.module.impl.movement.jesus;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.EventStage;
import nebula.client.listener.event.player.EventMoveUpdate;
import nebula.client.listener.event.world.EventCollision;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import net.minecraft.util.AxisAlignedBB;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Jesus",
  description = "Allows you to walk on water")
public class JesusModule extends Module {

  /**
   * The bounding box offset for a liquid block
   */
  private static final AxisAlignedBB JESUS_AABB = new AxisAlignedBB(
    0, 0, 0, 1, 0.99, 1);

  @Subscribe
  private final Listener<EventCollision> collision = event -> {
    if (event.entity() != null
      && event.entity().equals(mc.thePlayer)
      && event.block().getMaterial().isLiquid()) {

      event.setBox(JESUS_AABB.copy().offset(event.x(), event.y(), event.z()));
    }
  };

  @Subscribe
  private final Listener<EventMoveUpdate> moveUpdate = event -> {
    if (isAboveWater() && event.stage() == EventStage.PRE) {

      event.setGround(false);

      if (mc.thePlayer.ticksExisted % 2 == 0) {
        event.setY(event.y() + 0.01);
        event.setStance(event.stance() + 0.01);
      }
    }
  };

  public static boolean isAboveWater() {
    double x = Math.floor(mc.thePlayer.posX);
    double y = Math.round(mc.thePlayer.boundingBox.minY) - 1;
    double z = Math.floor(mc.thePlayer.posZ);
    return mc.theWorld.getBlock((int) x, (int) y, (int) z).getMaterial().isLiquid();
  }
}
