package us.nebula.impl.cheat.movement;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "NoJumpDelay",
        description = "Removes the vanilla jump delay",
        category = CheatCategory.MOVEMENT)
public final class NoJumpDelayCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.thePlayer.jumpTicks = 0;
    };
}
