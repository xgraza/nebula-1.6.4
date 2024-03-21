package nebula.client.module.impl.player.autoreconnect;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventChangeServer;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.math.Timer;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.client.multiplayer.ServerData;

/**
 * @author Gavin
 * @since 08/24/23
 */
@ModuleMeta(name = "AutoReconnect",
  description = "Automatically reconnects to the previous server you were on")
public class AutoReconnectModule extends Module {

  @SettingMeta("Delay")
  public final Setting<Double> delay = new Setting<>(
    5.0, 0.0, 10.0, 0.01);

  private final Timer timer = new Timer();
  private ServerData lastServer;

  @Subscribe
  private final Listener<EventChangeServer> changeServer = event -> {
    if (event.serverData() == null) return;
    lastServer = event.serverData();
  };

  public Timer timer() {
    return timer;
  }

  public ServerData lastServer() {
    return lastServer;
  }

  public boolean canReconnect() {
    return macro().toggled() && lastServer != null;
  }
}
