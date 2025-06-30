package us.nebula.impl.cheat.render;

import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.render.EventRender2D;

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
}
