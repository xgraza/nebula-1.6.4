package nebula.client.module.impl.render.norender;

import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRender3DItem;
import nebula.client.listener.event.render.EventRenderEXPOrb;
import nebula.client.listener.event.render.overlay.EventRenderBurning;
import nebula.client.listener.event.render.overlay.EventRenderHurtCamera;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "NoRender",
  description = "Prevents annoying things from rendering")
public class NoRenderModule extends Module {

  @SettingMeta("Hurt Camera")
  private final Setting<Boolean> hurtCamera = new Setting<>(
    true);
  @SettingMeta("Burning")
  private final Setting<Boolean> burning = new Setting<>(
    true);
  @SettingMeta("Items")
  private final Setting<Boolean> items = new Setting<>(
    false);
  @SettingMeta("EXP")
  private final Setting<Boolean> exp = new Setting<>(
    false);

  // listeners

  @Subscribe
  private final Listener<EventRenderHurtCamera> renderHurtCamera
    = event -> event.setCanceled(hurtCamera.value());

  @Subscribe
  private final Listener<EventRenderBurning> renderBurning
    = event -> event.setCanceled(burning.value());

  @Subscribe
  private final Listener<EventRender3DItem> render3DItem
    = event -> event.setCanceled(items.value());

  @Subscribe
  private final Listener<EventRenderEXPOrb> renderEXPOrb
    = event -> event.setCanceled(exp.value());
}
