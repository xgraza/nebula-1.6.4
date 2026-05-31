package us.nebula.client.cheat.impl.movement;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.game.EventUpdate;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/24/25
 */
@CheatManifest(name = "NoJumpDelay",
        description = "Removes the vanilla jump delay",
        category = CheatCategory.MOVEMENT)
public final class NoJumpDelayCheat extends Cheat
{
    private final Setting<Boolean> horsesSetting = builder("Horses", false)
            .setDescription("If to remove the jump delay on horses")
            .build();

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
