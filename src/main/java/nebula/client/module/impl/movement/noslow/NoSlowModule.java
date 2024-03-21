package nebula.client.module.impl.movement.noslow;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventSlowdown;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "NoSlow",
  description = "Prevents you from getting slowed down")
public class NoSlowModule extends Module {

  @Subscribe
  private final Listener<EventSlowdown> slowdown = event -> {
    event.input().moveForward *= 5.0f;
    event.input().moveStrafe *= 5.0f;
  };
}
