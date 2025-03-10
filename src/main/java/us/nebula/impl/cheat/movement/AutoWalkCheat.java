package us.nebula.impl.cheat.movement;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

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
    protected void onDisable()
    {
        super.onDisable();
        MC.gameSettings.keyBindForward.pressed = false;
    }

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.gameSettings.keyBindForward.pressed = true;
    };
}
