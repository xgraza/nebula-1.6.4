package nebula.client.module.impl.player.nofall;

import nebula.client.gui.module.future.setting.EnumSettingComponent;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventMoveUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "NoFall",
  description = "Prevents fall damage")
public class NoFallModule extends Module {

  @SettingMeta("Mode")
  private final Setting<NoFallMode> mode = new Setting<>(
    NoFallMode.SPOOF);
  @SettingMeta("Distance")
  private final Setting<Float> distance = new Setting<>(
    3.0f, 0.0f, 20.0f, 0.1f);

  @Override
  public String info() {
    return EnumSettingComponent.format(mode.value()) + " " + distance.value();
  }

  @Subscribe
  private final Listener<EventMoveUpdate> moveUpdate = event -> {
    if (mc.thePlayer.fallDistance < distance.value()) return;

    switch (mode.value()) {
      case SPOOF -> event.setGround(true);
      case LAG_BACK -> {
        event.setY(event.y() + 0.42f);
        event.setStance(event.stance() + 0.42f);
      }
      case NAN -> mc.thePlayer.sendQueue.addToSendQueueSilent(
        new C03PacketPlayer.C04PacketPlayerPosition(
          Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
    }

    mc.thePlayer.fallDistance = 0.0f;
  };
}
