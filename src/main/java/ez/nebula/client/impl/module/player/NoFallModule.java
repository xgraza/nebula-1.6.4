package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.player.EventMoveUpdate;
import ez.nebula.client.api.setting.EnumSetting;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.impl.gui.module.component.module.value.EnumSettingComponent;

/**
 * @author xgraza
 * @since 03/13/25
 */
@ModuleManifest(name = "NoFall",
        description = "Attempts to negate or entirely prevent fall damage",
        category = ModuleCategory.PLAYER)
public final class NoFallModule extends Module
{
    private final EnumSetting<Mode> modeSetting = enumBuilder("Mode", Mode.SPOOF)
            .setDescription("The way to negate fall damage")
            .build();
    private final NumberSetting<Float> fallDistanceSetting = numberBuilder("Fall Distance", 3.0f)
            .setMin(0.5f)
            .setMax(32.0f)
            .setScale(0.5f)
            .setDescription("The minimum fall distance to try to negate fall damage at")
            .build();

    @Subscribe
    private final EventListener<EventMoveUpdate> moveUpdateEventListener = event ->
    {
        if (MC.thePlayer.fallDistance < fallDistanceSetting.getValue())
        {
            return;
        }

        switch (modeSetting.getValue())
        {
            case SPOOF:
            {
                event.setOnGround(true);
                break;
            }
            case LAGBACK:
            {
                event.setY(event.getY() + 3);
                event.setStance(event.getStance() + 3);
                break;
            }
        }
        MC.thePlayer.fallDistance = 0.0f;
    };

    @Override
    public String getMetadata()
    {
        return EnumSettingComponent.formatEnum(modeSetting.getValue());
    }

    private enum Mode
    {
        SPOOF, LAGBACK
    }
}
