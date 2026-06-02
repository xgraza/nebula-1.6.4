package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 06/24/25
 */
@ModuleManifest(name = "Yaw",
        description = "Locks your yaw to the nearest cardinal direction",
        category = ModuleCategory.PLAYER)
public final class YawModule extends Module
{
    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.thePlayer.rotationYaw = Math.round((MC.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
}
