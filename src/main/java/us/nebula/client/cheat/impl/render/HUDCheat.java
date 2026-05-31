package us.nebula.client.cheat.impl.render;

import us.nebula.client.Nebula;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.listener.event.render.EventRender2D;
import us.nebula.client.hud.gui.HUDEditorScreen;
import us.nebula.client.setting.Setting;
import us.nebula.client.util.render.ColorUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 02/26/25
 */
@CheatManifest(name = "HUD",
        description = "Displays important information in the game GUI",
        category = CheatCategory.RENDER)
public final class HUDCheat extends Cheat
{
    @CheatInstance
    public static HUDCheat INSTANCE;

    public final Setting<Color> primaryColorSetting = colorBuilder("Primary Color", new Color(112, 82, 143))
            .setDescription("The primary client color")
            .build();
    public final Setting<ColorMode> colorModeSetting = enumBuilder("Color Mode", ColorMode.STATIC)
            .setDescription("The client color mode")
            .build();
    public final Setting<Float> minBrightnessSetting = numberBuilder("Minimum Brightness", 0.65f)
            .setMin(0.05f)
            .setMax(0.95f)
            .setScale(0.05f)
            .setDescription("The minimum brightness for the gradient rainbow")
            .setVisibility((value) -> colorModeSetting.getValue() == ColorMode.GRADIENT_RAINBOW)
            .build();
    public final Setting<Double> speedSetting = numberBuilder("Speed", 2.5)
            .setMin(1.0)
            .setMax(10.0)
            .setScale(0.5)
            .setDescription("The speed at which the rainbow should go")
            .setVisibility((value) -> colorModeSetting.getValue() == ColorMode.RAINBOW)
            .build();
    public final Setting<Boolean> forceInBoundsSetting = builder("Force in bounds", true)
            .setDescription("Force in bounds")
            .build();

    public HUDCheat()
    {
        toggle();
    }

    @Override
    public void discoverSettings()
    {
        super.discoverSettings();
        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            final Setting<Boolean> setting = new Setting.Builder<>(element.getManifest().name(), false)
                    .build();
            registerSetting(setting);
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
