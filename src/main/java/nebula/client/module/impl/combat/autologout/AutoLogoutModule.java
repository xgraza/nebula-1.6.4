package nebula.client.module.impl.combat.autologout;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.player.EventUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.chat.ChatUtils;
import nebula.client.util.chat.Printer;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.util.ChatComponentText;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "AutoLogout",
  description = "Automatically logs out of the game upon low health")
public class AutoLogoutModule extends Module {

  @SettingMeta("Health")
  private final Setting<Float> health = new Setting<>(
    6.0f, 0.0f, 19.0f, 0.5f);
  @SettingMeta("Auto Disable")
  private final Setting<Boolean> autoDisable = new Setting<>(
    true);

  @Override
  public String info() {
    return String.valueOf(health.value());
  }

  @Subscribe
  private final Listener<EventUpdate> update = event -> {
    if (mc.thePlayer.getHealth() <= health.value()) {
      mc.thePlayer.sendQueue.getNetworkManager().closeChannel(
        new ChatComponentText(ChatUtils.replaceFormatting(
          Printer.PREFIX
            + "automatically logged out at &c"
            + String.format("%.1f", mc.thePlayer.getHealth())
            + "\u2764")));

      if (autoDisable.value()) macro().setEnabled(false);
    }
  };
}
