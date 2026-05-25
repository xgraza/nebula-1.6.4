package us.nebula.client.cheat.impl.player;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "Yaw",
        description = "Locks your yaw to the nearest cardinal direction",
        category = CheatCategory.PLAYER)
public final class YawCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.thePlayer.rotationYaw = Math.round((MC.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
}
