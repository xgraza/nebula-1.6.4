package us.nebula.client.cheat.impl.movement;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.listener.event.player.EventSafeWalk;
import us.nebula.client.util.value.Setting;

/**
 * @author xgraza
 * @since 05/26/26
 */
@CheatManifest(name = "SafeWalk",
        description = "Adds the safeguard of sneaking to prevent falling without having to sneak",
        category = CheatCategory.MOVEMENT)
public final class SafeWalkCheat extends Cheat
{
    private final Setting<Boolean> onlyOnGroundSetting = new Setting<>(
            "Only On Ground", true);

    @Subscribe
    private final EventListener<EventSafeWalk> safeWalkEventListener = event ->
    {
        if (onlyOnGroundSetting.getValue() && !MC.thePlayer.onGround)
        {
            return;
        }
        event.cancel();
    };
}
