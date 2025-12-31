package us.nebula.client.impl.cheat.player;

import us.nebula.client.ClientSettings;
import us.nebula.client.api.listener.EventListener;
import us.nebula.client.api.listener.Subscribe;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.value.Setting;
import us.nebula.client.impl.event.player.EventMoveUpdate;
import us.nebula.client.impl.gui.client.component.cheat.value.EnumSettingComponent;
import us.nebula.client.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/13/25
 */
@CheatManifest(name = "NoFall",
        description = "Negates/prevents fall damage",
        category = CheatCategory.PLAYER)
public final class NoFallCheat extends Cheat
{
    private final Setting<Float> fallDistanceSetting = new Setting<>(
            "Fall Distance", 3.0f, 0.5f, 32.0f, 0.5f);
    private final Setting<Mode> modeSetting = new Setting<>(
            "Mode", Mode.SPOOF);

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
                if (ClientSettings.VERBOSE_LOGGING)
                {
                    ChatUtil.send("Attempting lagback...");
                }
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
