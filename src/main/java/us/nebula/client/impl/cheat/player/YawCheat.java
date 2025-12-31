package us.nebula.client.impl.cheat.player;

import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.impl.event.game.EventUpdate;

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
