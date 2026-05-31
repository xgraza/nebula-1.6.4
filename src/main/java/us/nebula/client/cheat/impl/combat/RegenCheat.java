package us.nebula.client.cheat.impl.combat;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;
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
    private final Setting<Float> healthSetting = numberBuilder("Health", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.5f)
            .setDescription("At what health should you automatically be logged off")
            .build();

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
