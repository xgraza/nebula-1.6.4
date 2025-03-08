package us.nebula.impl.cheat.player;

import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/08/25
 */
@CheatManifest(name = "FastPlace",
        description = "Removes the place/use delay in vanilla MC",
        category = CheatCategory.PLAYER)
public final class FastPlaceCheat extends Cheat
{
    private final Setting<Integer> delaySetting = new Setting<>(
            "Delay", 0, 0, 4, 1);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.rightClickDelayTimer = delaySetting.getValue();
    };
}
