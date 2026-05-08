package us.nebula.client.cheat.impl.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.game.EventUpdate;

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

    @Override
    public String getMetadata()
    {
        return String.format("%.2f", healthSetting.getValue());
    }
}
