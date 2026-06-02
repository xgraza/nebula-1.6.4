package ez.nebula.client.impl.module.combat;

import ez.nebula.client.api.manager.module.Module;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import ez.nebula.client.api.listener.EventListener;
import ez.nebula.client.api.listener.Subscribe;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.listener.event.network.EventPacket;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/02/25
 */
@ModuleManifest(name = "Velocity",
        description = "Negates/cancels knockback & explosion velocity",
        category = ModuleCategory.COMBAT)
public final class VelocityModule extends Module
{
    private final Setting<Boolean> knockbackSetting = builder("Knockback", true)
            .setDescription("If to ignore knockback (i.e. player attacks)")
            .build();
    private final Setting<Boolean> explosionSetting = builder("Explosions", true)
            .setDescription("If to ignore explosion knockback (i.e. creeper explosions)")
            .build();

    @Subscribe
    private final EventListener<EventPacket.Inbound> inboundEventListener = event ->
    {
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            return;
        }
        if (event.getPacket() instanceof S12PacketEntityVelocity)
        {
            final S12PacketEntityVelocity packet = event.getPacket();
            if (!knockbackSetting.getValue() || packet.getEntityId() != MC.thePlayer.getEntityId())
            {
                return;
            }
            event.cancel();
        } else if (event.getPacket() instanceof S27PacketExplosion)
        {
            if (explosionSetting.getValue())
            {
                event.cancel();
            }
        }
    };
}
