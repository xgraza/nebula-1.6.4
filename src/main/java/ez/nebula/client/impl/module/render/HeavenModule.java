package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.DebugFeature;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.player.EventPlayerDeath;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;

/**
 * @author xgraza
 * @since 9/1/26
 */
@DebugFeature
@ModuleManifest(name = "Heaven",
        description = "Makes players go to heaven :pray:",
        category = ModuleCategory.RENDER)
public final class HeavenModule extends Module
{
    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (event.getPlayer() != null && event.getPlayer().isDead)
        {
            event.getPlayer().motionY = 10;
        }
    };
}
