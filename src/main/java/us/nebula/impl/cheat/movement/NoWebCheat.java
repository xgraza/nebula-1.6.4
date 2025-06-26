package us.nebula.impl.cheat.movement;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 06/25/24
 */
@CheatManifest(name = "NoWeb",
        description = "Prevents you from getting stuck in a web",
        category = CheatCategory.MOVEMENT)
public final class NoWebCheat extends Cheat
{
    private final Setting<Boolean> ncpSetting = new Setting<>(
            "NCP", false);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.isInWeb && ncpSetting.getValue())
        {
            MC.thePlayer.motionY *= 0.05000000074505806D;
        }
        MC.thePlayer.isInWeb = false;
    };
}
