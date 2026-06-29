package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.input.EventUpdateInput;
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
        if (MC.thePlayer != null && !MC.gameSettings.keyBindForward.pressed)
        {
            MC.thePlayer.movementInput.moveForward = 0.0f;
        }
    }

    @Subscribe
    private final EventListener<EventUpdateInput.Post> postUpdateInputEventListener = event ->
    {
        if (event.getInput().equals(MC.thePlayer.movementInput))
        {
            event.getInput().moveForward = 1.0f;
        }
    };
}
