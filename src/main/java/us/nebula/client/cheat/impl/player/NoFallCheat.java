package us.nebula.client.cheat.impl.player;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.player.EventMoveUpdate;
import us.nebula.client.cheat.gui.component.cheat.value.EnumSettingComponent;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "NoFall",
        description = "Attempts to negate or entirely prevent fall damage",
        category = CheatCategory.PLAYER)
public final class NoFallCheat extends Cheat
{
    private final Setting<Mode> modeSetting = enumBuilder("Mode", Mode.SPOOF)
            .setDescription("The way to negate fall damage")
            .build();
    private final Setting<Float> fallDistanceSetting = numberBuilder("Fall Distance", 3.0f)
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
