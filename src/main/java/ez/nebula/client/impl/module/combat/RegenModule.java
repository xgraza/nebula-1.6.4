package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.network.play.client.C03PacketPlayer;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;
import ez.nebula.client.api.listener.event.game.EventUpdate;

/**
 * @author xgraza
 * @since 03/02/25
 */
@ModuleManifest(name = "Regen",
        description = "Regenerates health quickly with fabricated packets",
        category = ModuleCategory.COMBAT)
public final class RegenModule extends Module
{
    private final Setting<Float> healthSetting = numberBuilder("Health", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.5f)
            .setDescription("At what health to begin to regenerate health")
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
