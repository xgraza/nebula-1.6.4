package us.nebula.client.impl.cheat.movement;

import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/10/25
 */
@CheatManifest(name = "AutoWalk",
        description = "Automatically walks for you",
        category = CheatCategory.MOVEMENT)
public final class AutoWalkCheat extends Cheat
{
    @Override
    public void onDisable()
    {
        super.onDisable();
        if (MC.thePlayer == null)
        {
            return;
        }
        MC.thePlayer.movementInput.moveForward = 0.0f;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
            MC.thePlayer.movementInput.moveForward = 1.0f;
}
