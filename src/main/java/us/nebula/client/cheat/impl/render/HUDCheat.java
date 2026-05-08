package us.nebula.client.cheat.impl.render;

import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.render.EventRender2D;
import us.nebula.client.hud.gui.HUDEditorScreen;
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
        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            final Setting<Boolean> setting = new Setting<>(element.getManifest().name(), false)
                    .onValueChange((old, value) -> element.setToggled(value));
            addSetting(setting);
            element.setToggledSetting(setting);
        }
    }

    @Subscribe
    private final EventListener<EventRender2D> render2DEventListener = event ->
    {
        if (MC.gameSettings.showDebugInfo || MC.currentScreen instanceof HUDEditorScreen)
        {
            return;
        }
        MC.mcProfiler.startSection("nebulaRenderHUD");
        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            if (element.isToggled())
            {
                element.render(event.getResolution());
            }
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
