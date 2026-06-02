package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/10/25
 */
@ModuleManifest(name = "AutoWalk",
        description = "Automatically walks for you",
        category = ModuleCategory.MOVEMENT)
public final class AutoWalkModule extends Module
{
    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.gameSettings == null)
        {
            return;
        }
        MC.gameSettings.keyBindForward.pressed = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.gameSettings.keyBindForward.pressed = true;
}
