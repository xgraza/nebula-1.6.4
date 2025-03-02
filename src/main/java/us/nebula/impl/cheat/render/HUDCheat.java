package us.nebula.impl.cheat.render;

import us.nebula.Nebula;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.manager.overlay.Overlay;
import us.nebula.impl.event.render.EventRender2D;

/**
 * @author xgraza
 * @since 02/26/25
 */
@CheatManifest(name = "HUD",
        description = "Renders an information over the game GUI",
        category = CheatCategory.RENDER)
public final class HUDCheat extends Cheat
{
    @CheatInstance
    public static HUDCheat INSTANCE;

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
        if (MC.currentScreen != null || MC.gameSettings.showDebugInfo)
        {
            return;
        }
        for (final Overlay overlay : Nebula.INSTANCE.getOverlayManager().getAll())
        {
            if (!overlay.getStateSetting().getValue())
            {
                return;
            }
            overlay.render(event.getResolution(), event.getPartialTicks());
        }
    };
}
