package nebula.client.module.impl.movement.autowalk;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "AutoWalk", description = "Automatically walks for you")
public class AutoWalkModule extends Module {

  @Override
  public void disable() {
    super.disable();
    mc.gameSettings.keyBindForward.pressed = false;
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    mc.gameSettings.keyBindForward.pressed = true;
  };
}
