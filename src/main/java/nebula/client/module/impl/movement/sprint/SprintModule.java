package nebula.client.module.impl.movement.sprint;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Sprint", defaultState = true)
public class SprintModule extends Module {

  @SettingMeta("Rage")
  private final Setting<Boolean> rage = new Setting<>(
    false);

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    mc.gameSettings.keyBindSprint.pressed = true;

    if (rage.value() && !mc.thePlayer.isSprinting()) {
      mc.thePlayer.setSprinting(true);
    }
  };
}
