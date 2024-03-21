package nebula.client.module.impl.render.infviewer;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRenderStackCount;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.module.impl.render.hud.HUDModule;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

import static nebula.client.util.player.ItemUtils.infinite;

/**
 * @author Gavin
 * @since 08/18/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "InfViewer",
  description = "Allows you to view infinite item stack counts")
public class InfViewerModule extends Module {

  @SettingMeta("Simple")
  private final Setting<Boolean> simple = new Setting<>(
    false);

  @Subscribe
  private final Listener<EventRenderStackCount> renderStackCount = event -> {
    if (!infinite(event.itemStack())) return;

    event.setColor(HUDModule.primary.value().getRGB());
    event.setText(simple.value()
      ? "-"
      : String.valueOf(event.itemStack().stackSize));
    event.setCanceled(true);
  };
}
