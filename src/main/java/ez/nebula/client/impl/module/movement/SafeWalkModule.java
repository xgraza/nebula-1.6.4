package ez.nebula.client.impl.module.movement;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.player.EventSafeWalk;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 05/26/26
 */
@ModuleManifest(name = "SafeWalk",
        description = "Adds the safeguard of sneaking to prevent falling without having to sneak",
        category = ModuleCategory.MOVEMENT)
public final class SafeWalkModule extends Module
{
    private final Setting<Boolean> onlyOnGroundSetting = builder("Only On Ground", true)
            .setDescription("If to only safe walk when on ground")
            .build();

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
