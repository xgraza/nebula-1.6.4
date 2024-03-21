package nebula.client.module.impl.combat.regen;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 08/26/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "Regen",
  description = "Regenerates your health faster than vanilla")
public class RegenModule extends Module {

  @SettingMeta("Health")
  private final Setting<Float> health = new Setting<>(
    16.0f, 1.0f, 19.5f, 0.5f);
  @SettingMeta("Conserve")
  private final Setting<Boolean> conserve = new Setting<>(
    true);

  @Override
  public String info() {
    return String.valueOf(health.value());
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mc.thePlayer.getHealth() > health.value()
      || mc.thePlayer.isUsingItem()) return;

    for (int i = 0; i < (conserve.value() ? 20 - Math.floor(health.value()) : 20); ++i) {
      mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer());
    }
  };
}
