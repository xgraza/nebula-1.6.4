package us.nebula.client.cheat.impl.movement;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;

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
