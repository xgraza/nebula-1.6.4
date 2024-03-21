package nebula.client.module.impl.movement.icespeed;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventMoveBlockFriction;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import net.minecraft.block.BlockIce;

/**
 * @author Gavin
 * @since 08/17/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "IceSpeed",
  description = "Makes you go zoom on ice",
  defaultState = true)
public class IceSpeedModule extends Module {
  public static final float NCP_ICE_MAX = 0.391f; // this could prolly be faster but yk

  @Subscribe
  private final Listener<EventMoveBlockFriction> moveBlockFriction = event -> {
    if (event.block() instanceof BlockIce) {
      event.setSlipperiness(NCP_ICE_MAX);
    }
  };
}
