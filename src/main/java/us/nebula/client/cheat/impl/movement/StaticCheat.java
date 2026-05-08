package us.nebula.client.cheat.impl.movement;

import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.player.EventMove;
import us.nebula.client.util.player.MoveUtil;

/**
 * @author xgraza
 * @since 07/04/25
 */
@CheatManifest(name = "Static",
        description = "Makes movement more instantaneous",
        category = CheatCategory.MOVEMENT)
public final class StaticCheat extends Cheat
{
    private final Setting<Boolean> movingSetting = new Setting<>(
            "Moving", false);

    @Subscribe
    private final EventListener<EventMove> moveEventListener = event ->
    {
        if (MoveUtil.isMoving())
        {
            if (!movingSetting.getValue()
                    || !MC.thePlayer.onGround
                    || MC.gameSettings.keyBindJump.pressed
                    || SpeedCheat.INSTANCE.isToggled())
            {
                return;
            }
            MoveUtil.setSpeed(event, MoveUtil.getBaseNcpSpeed(0));
            return;
        }
        MoveUtil.setSpeed(event, 0);
    };
}
