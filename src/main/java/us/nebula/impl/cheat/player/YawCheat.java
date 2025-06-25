package us.nebula.impl.cheat.player;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Yaw",
        description = "Locks your yaw to the nearest direction",
        category = CheatCategory.PLAYER)
public final class YawCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.thePlayer.rotationYaw = Math.round((MC.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
    };
}
