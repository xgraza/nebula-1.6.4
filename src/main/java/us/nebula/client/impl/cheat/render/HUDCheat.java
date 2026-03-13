package us.nebula.client.impl.cheat.render;

import us.nebula.client.Nebula;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.manager.overlay.Overlay;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.render.EventRender2D;
import us.nebula.client.util.render.ColorUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 02/26/25
 */
@CheatManifest(name = "HUD",
        description = "Renders information over the game GUI",
        category = CheatCategory.RENDER)
public final class HUDCheat extends Cheat
{
    @CheatInstance
    public static HUDCheat INSTANCE;

    public final Setting<Color> primaryColorSetting = new Setting<>(
            "Primary Color", new Color(112, 82, 143));
    public final Setting<ColorMode> colorModeSetting = new Setting<>(
            "Color Mode", ColorMode.STATIC);
    public final Setting<Float> minBrightnessSetting = new Setting<>(
            "Minimum Brightness", 0.65f, 0.05f, 0.95f, 0.05f)
            .setVisibility(() -> colorModeSetting.getValue() == ColorMode.GRADIENT_RAINBOW);
    public final Setting<Double> speedSetting = new Setting<>(
            "Speed", 2.5, 1.0, 10.0, 0.5)
            .setVisibility(() -> colorModeSetting.getValue() == ColorMode.RAINBOW);

    public HUDCheat()
    {
        toggle();
        for (final Overlay overlay : Nebula.INSTANCE.getOverlayManager().getAll())
        {
            addSetting(overlay.getStateSetting());
        }
    }

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (MC.gameSettings.showDebugInfo)
        {
            return;
        }
        MC.mcProfiler.startSection("nebulaRenderHUD");
        for (final Overlay overlay : Nebula.INSTANCE.getOverlayManager().getAll())
        {
            if (!overlay.getStateSetting().getValue())
            {
                continue;
            }
            MC.mcProfiler.startSection(overlay.getManifest().value());
            overlay.render(event.getResolution(), event.getPartialTicks());
            MC.mcProfiler.endSection();
        }
        MC.mcProfiler.endSection();
    };

    public int getPrimary()
    {
        return primaryColorSetting.getValue().getRGB();
    }

    public int getBaseColor(int meta)
    {
        switch (colorModeSetting.getValue())
        {
            case STATIC:
                return getPrimary();

            case WHITE:
                return Color.white.getRGB();

            case RAINBOW:
                return ColorUtil.rainbowCycle(meta, speedSetting.getValue());

            case GRADIENT_RAINBOW:
                return ColorUtil.gradientRainbow(primaryColorSetting.getValue(), minBrightnessSetting.getValue(), meta);
        }
        return getPrimary();
    }

    public enum ColorMode
    {
        STATIC, WHITE, RAINBOW, GRADIENT_RAINBOW
    }
}
