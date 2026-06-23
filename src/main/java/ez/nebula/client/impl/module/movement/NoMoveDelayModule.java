package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.IEventPriorities;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.player.EventMoveUpdate;

/**
 * @author xgraza
 * @since 5/4/26
 */
@ModuleManifest(name = "NoMoveDelay",
        description = "Removes a vanilla move minimum to send a new move packet to the server",
        category = ModuleCategory.MOVEMENT)
public final class NoMoveDelayModule extends Module
{
    @Subscribe(priority = IEventPriorities.MEDIUM)
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
            event.setMinMove(0.0);
}
