package us.nebula.client.cheat.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.listener.event.network.EventPacket;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "Velocity",
        description = "Negates/cancels knockback & explosion velocity",
        category = CheatCategory.COMBAT)
public final class VelocityCheat extends Cheat
{
    private final Setting<Boolean> knockbackSetting = new Setting<>(
            "Knockback", true);
    private final Setting<Boolean> explosionSetting = new Setting<>(
            "Explosions", true);

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
