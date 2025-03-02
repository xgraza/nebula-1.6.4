package us.nebula.impl.cheat.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "Regen",
        description = "Regenerates health quickly with fabricated packets",
        category = CheatCategory.COMBAT)
public final class RegenCheat extends Cheat
{
    private final Setting<Float> healthSetting = new Setting<>(
            "Health", 8.0f, 1.0f, 19.5f, 0.01f);

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.getHealth() > healthSetting.getValue())
        {
            return;
        }
        for (int i = 0; i < 20; ++i)
        {
            MC.thePlayer.sendQueue.addToSendQueue(
                    new C03PacketPlayer(MC.thePlayer.onGround));
        }
    };
}
