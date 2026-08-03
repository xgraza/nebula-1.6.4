package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.listener.EventBus;
import ez.nebula.client.api.listener.event.game.EventTick;
import ez.nebula.client.api.setting.ColorSetting;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.api.listener.event.render.EventRender2D;
import ez.nebula.client.impl.gui.hud.HUDEditorScreen;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.util.render.ColorUtil;
import ez.nebula.client.util.render.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.Color;

/**
 * @author xgraza
 * @since 02/26/25
 */
@ModuleManifest(name = "HUD",
        description = "Displays important information in the game GUI",
        category = ModuleCategory.RENDER)
public final class HUDModule extends Module
{
    @ModuleInstance
    public static HUDModule INSTANCE;

    public final ColorSetting primaryColorSetting = colorBuilder("Primary Color", new Color(112, 82, 143))
            .setExemptClientSync(true)
            .setAllowTransparency(false)
            .setDescription("The primary client color")
            .build();
    public final EnumSetting<ColorMode> colorModeSetting = enumBuilder("Color Mode", ColorMode.STATIC)
            .setDescription("The client color mode")
            .build();
    public final NumberSetting<Float> minBrightnessSetting = numberBuilder("Minimum Brightness", 0.65f)
            .setMin(0.05f)
            .setMax(0.95f)
            .setScale(0.05f)
            .setDescription("The minimum brightness for the gradient rainbow")
            .setVisibility((value) -> colorModeSetting.getValue() == ColorMode.GRADIENT_RAINBOW)
            .build();
    public final NumberSetting<Double> speedSetting = numberBuilder("Speed", 2.5)
            .setMin(1.0)
            .setMax(10.0)
            .setScale(0.5)
            .setDescription("The speed at which the rainbow should go")
            .setVisibility((value) -> colorModeSetting.getValue() == ColorMode.RAINBOW)
            .build();
    public final Setting<Boolean> forceInBoundsSetting = builder("Force in bounds", true)
            .setDescription("Force in bounds when moving around a HUD element")
            .build();

    public double prevWidth = -1, prevHeight = -1;

    public HUDModule()
    {
        // automatically toggle & hide
        setHidden(true);
        setToggled(true);

        EventBus.subscribe(new Object()
        {
            private boolean wasPrevNull;

            @Subscribe
            private final EventListener<EventTick> tickEventListener = event ->
            {
                if (RenderUtil.GAME_RESOLUTION == null)
                {
                    wasPrevNull = true;
                    return;
                }
                // scale hud elements automatically
                final double width = RenderUtil.GAME_RESOLUTION.getScaledWidth_double();
                final double height = RenderUtil.GAME_RESOLUTION.getScaledHeight_double();
                if (wasPrevNull)
                {
                    wasPrevNull = false;
                    prevHeight = height;
                    prevWidth = width;
                }
                if (prevHeight != -1 && prevWidth != -1 && (width != prevWidth || height != prevHeight))
                {
                    final double scaleX = width / prevWidth;
                    final double scaleY = height / prevHeight;
                    for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
                    {
                        element.setX(element.getX() * scaleX);
                        element.setY(element.getY() * scaleY);
                        if (forceInBoundsSetting.getValue())
                        {
                            if (element.getX() < 0)
                            {
                                element.setX(0);
                            }

                            if (element.getY() < 0)
                            {
                                element.setY(0);
                            }

                            if (element.getX() + element.getWidth() > width)
                            {
                                element.setX(width - element.getWidth());
                            }

                            if (element.getY() + element.getHeight() > height)
                            {
                                element.setY(height - element.getHeight());
                            }
                        }
                    }
                }
                prevWidth = width;
                prevHeight = height;
            };
        });
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
