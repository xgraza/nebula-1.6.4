package us.nebula.client.cheat.impl.world;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 05/21/26
 */
@CheatManifest(name = "StashHunter",
        description = "Aids in finding stashes",
        category = CheatCategory.WORLD)
public final class StashHunterCheat extends Cheat
{

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {

    };
}
