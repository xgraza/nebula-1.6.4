package us.nebula.client.cheat.impl.movement;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "NoJumpDelay",
        description = "Removes the vanilla jump delay",
        category = CheatCategory.MOVEMENT)
public final class NoJumpDelayCheat extends Cheat
{
    private final Setting<Boolean> horsesSetting = new Setting<>(
            "Horses", false);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        MC.thePlayer.jumpTicks = 0;

        if (horsesSetting.getValue() && MC.thePlayer.isRidingHorse())
        {
            MC.thePlayer.horseJumpPowerCounter = 9;
        }
    };
}
