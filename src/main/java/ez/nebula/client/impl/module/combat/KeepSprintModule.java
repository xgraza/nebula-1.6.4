package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.player.EventAttackSprint;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 9/11/26
 */
@ModuleManifest(name = "KeepSprint",
        description = "Keeps your sprint when attacking",
        category = ModuleCategory.COMBAT)
public final class KeepSprintModule extends Module
{
    private final Setting<Boolean> slowdownSetting = builder("Slowdown", true)
            .setDescription("If to allow the vanilla slowdown of a factor of 0.6 of your XZ movement")
            .build();

    @Subscribe
    private final EventListener<EventAttackSprint> attackSprintEventListener = event ->
    {
        event.setSlowdown(slowdownSetting.getValue());
        event.cancel();
    };
}
