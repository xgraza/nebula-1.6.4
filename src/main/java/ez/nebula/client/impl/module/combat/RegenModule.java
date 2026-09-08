package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.listener.event.game.EventUpdate;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.NumberSetting;
import ez.nebula.client.util.minecraft.network.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author xgraza
 * @since 03/02/25
 */
@ModuleManifest(name = "Regen",
        description = "Regenerates health quickly with fabricated packets",
        category = ModuleCategory.COMBAT)
public final class RegenModule extends Module
{
    private final NumberSetting<Float> healthSetting = numberBuilder("Health", 6.0f)
            .setMin(1.0f)
            .setMax(19.5f)
            .setScale(0.5f)
            .setDescription("At what health to begin to regenerate health")
            .build();
    private final NumberSetting<Integer> packetsSetting = numberBuilder("Packets", 20)
            .setMin(1)
            .setMax(100)
            .setScale(1)
            .setDescription("How many packets to send in a tick to regenerate health")
            .build();

    @Subscribe
    private final EventListener<EventUpdate> updateEventListener = event ->
    {
        if (MC.thePlayer.getHealth() > healthSetting.getValue())
        {
            return;
        }
        for (int i = 0; i < packetsSetting.getValue(); ++i)
        {
            PacketUtil.send(new C03PacketPlayer(MC.thePlayer.onGround));
        }
    };

    @Override
    public String getMetadata()
    {
        return String.format("%.2f", healthSetting.getValue());
    }
}
