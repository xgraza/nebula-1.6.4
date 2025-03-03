package us.nebula.impl.cheat.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.network.EventPacket;

/**
 * @author xgraza
 * @since 03/02/25
 */
@CheatManifest(name = "Velocity",
        description = "Negates knockback & explosion velocity",
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
