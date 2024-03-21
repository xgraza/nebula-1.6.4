package nebula.client.module.impl.render.hud;

import nebula.client.BuildConfig;
import nebula.client.Nebula;
import nebula.client.listener.bus.Listener;
import nebula.client.listener.bus.Subscribe;
import nebula.client.listener.event.render.EventRender2D;
import nebula.client.module.Module;
import nebula.client.module.ModuleMeta;
import nebula.client.util.render.ColorUtils;
import nebula.client.util.value.Setting;
import nebula.client.util.value.SettingMeta;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

/**
 * @author Gavin
 * @since 08/09/23
 */
@SuppressWarnings("unused")
@ModuleMeta(name = "HUD",
  description = "Renders a display over the vanilla overlay",
  defaultState = true)
public class HUDModule extends Module {

  @SettingMeta("Primary Color")
  public static final Setting<Color> primary = new Setting<>(
    new Color(239, 47, 47));

  @SettingMeta("Secondary Color")
  public static final Setting<Color> secondary = new Setting<>(
    new Color(222, 121, 121));

  @SettingMeta("Watermark")
  private final Setting<Boolean> watermark = new Setting<>(
    true);
  @SettingMeta("Arraylist")
  private final Setting<Boolean> arraylist = new Setting<>(
    true);

  @SettingMeta("Speed")
  private final Setting<Boolean> speed = new Setting<>(
    true);

  @Subscribe
  private final Listener<EventRender2D> render2D = event -> {

    if (mc.gameSettings.showDebugInfo) return;

    renderWatermark: {
      if (!watermark.value()) break renderWatermark;

      int x = mc.fontRenderer.drawStringWithShadow("N", 2, 2,
        ColorUtils.pulse(primary.value(), secondary.value(), 10, 0.5).getRGB());
      mc.fontRenderer.drawStringWithShadow("ebula " + BuildConfig.VERSION
        + "+" + BuildConfig.BUILD
        + "-" + BuildConfig.HASH
        + "/" + BuildConfig.BRANCH, x, 2, 0xAAAAAA);
    }

    renderArrayList: {
      if (!arraylist.value()) break renderArrayList;

      List<Module> modules = Nebula.INSTANCE.module.values()
        .stream()
        .filter((x) -> !x.hidden() && (x.macro().toggled() || x.animation().factor() > 0))
        .sorted(Comparator.comparingInt((x) -> -mc.fontRenderer.getStringWidth(formatModule(x))))
        .toList();

      if (modules.isEmpty()) break renderArrayList;

      int y = 2;
      for (int i = 0; i < modules.size(); ++i) {
        Module module = modules.get(i);
        String name = formatModule(module);

        double factor = module.animation().factor();

        mc.fontRenderer.drawStringWithShadow(name,
          (int) (event.resolution().getScaledWidth_double() - (mc.fontRenderer.getStringWidth(name) + 2) * factor),
          y,
          ColorUtils.pulse(primary.value(), secondary.value(),
            i * 10, 0.5).getRGB());
        y += (mc.fontRenderer.FONT_HEIGHT + 2) * factor;
      }
    }

    renderSpeed: {
      if (!speed.value()) break renderSpeed;

      double x = mc.thePlayer.posX - mc.thePlayer.lastTickPosX;
      double z = mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ;
      double moveSpeed = Math.sqrt(x * x + z * z);

      String format = String.format("BPS: %s%.2f", EnumChatFormatting.GRAY, moveSpeed * 20.0);
      mc.fontRenderer.drawStringWithShadow(
        format,
        (int) (event.resolution().getScaledWidth_double() - mc.fontRenderer.getStringWidth(format) - 2),
        event.resolution().getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 2,
        ColorUtils.pulse(primary.value(), secondary.value(),
          20, 0.5).getRGB()
      );
    }

  };

  private String formatModule(Module module) {
    String tag = module.meta().name();

    String info = module.info();
    if (info != null && !info.isEmpty()) {
      tag += " " + EnumChatFormatting.GRAY + info;
    }

    return tag;
  }
}
