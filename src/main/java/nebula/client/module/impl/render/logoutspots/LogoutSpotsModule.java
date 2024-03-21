package nebula.client.module.impl.render.logoutspots;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.net.EventTabListUpdate;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.chat.Printer;
import nebula.client.util.player.FakePlayerUtils;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "LogoutSpots",
  description = "Shows where people logged out")
public class LogoutSpotsModule extends Module {

  private final Map<String, LogoutSpot> logoutSpotMap = new ConcurrentHashMap<>();

  @Override
  public void disable() {
    super.disable();

    if (mc.theWorld != null) {
      for (String name : logoutSpotMap.keySet()) {
        LogoutSpot spot = logoutSpotMap.remove(name);
        if (spot == null) continue;

        FakePlayerUtils.despawn(spot.fakeId());
      }
    }

    logoutSpotMap.clear();
  }

  @Subscribe
  private final Listener<EventTabListUpdate> tabListUpdate = event -> {
    if (mc.thePlayer == null || mc.theWorld == null) return;

    if (event.action() == EventTabListUpdate.Action.REMOVE) {
      EntityPlayer player = null;
      for (EntityPlayer p : (List<EntityPlayer>) mc.theWorld.playerEntities) {
        if (p != null
          && !p.equals(mc.thePlayer)
          && p.getCommandSenderName().equals(event.info().name)) {

          player = p;
          break;
        }
      }

      if (player == null) {
        Printer.print("Could not resolve logout spot for " + event.info().name);
        return;
      }

      LogoutSpot spot = new LogoutSpot(player.getCommandSenderName(),
        player.hashCode() + mc.thePlayer.hashCode(),
        player.getPosition(1.0f));
      logoutSpotMap.put(spot.name(), spot);

      FakePlayerUtils.spawn(spot.fakeId(), player);

      Printer.print(format("%s logged out at XYZ: %.1f, %.1f, %.1f",
        spot.name(),
        spot.position().xCoord,
        spot.position().yCoord,
        spot.position().zCoord));

    } else if (event.action() == EventTabListUpdate.Action.ADD) {
      if (!logoutSpotMap.containsKey(event.info().name)) return;

      LogoutSpot spot = logoutSpotMap.remove(event.info().name);
      if (spot == null) return; // wtf

      FakePlayerUtils.despawn(spot.fakeId());

      Printer.print(format("%s logged back in at XYZ: %.1f, %.1f, %.1f",
        spot.name(),
        spot.position().xCoord,
        spot.position().yCoord,
        spot.position().zCoord));
    }
  };
}
