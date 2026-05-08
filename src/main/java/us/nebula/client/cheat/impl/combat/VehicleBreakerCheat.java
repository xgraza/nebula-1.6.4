package us.nebula.client.cheat.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.C02PacketUseEntity;
import us.nebula.client.listener.EventListener;
import us.nebula.client.listener.Subscribe;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.listener.event.network.EventPacket;

/**
 * @author xgraza
 * @since 06/24/25
 */
@CheatManifest(name = "VehicleBreaker",
        description = "Quickly breaks vehicles",
        category = CheatCategory.COMBAT)
public final class VehicleBreakerCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventPacket.Outbound> outboundEventListener = event ->
    {
        if (event.getPacket() instanceof C02PacketUseEntity)
        {
            final C02PacketUseEntity packet = event.getPacket();
            final Entity entity = packet.func_149564_a(MC.theWorld);
            if (packet.getAction() != C02PacketUseEntity.Action.ATTACK || !(entity instanceof EntityBoat))
            {
                return;
            }
            for (int i = 0; i < 10; ++i)
            {
                MC.thePlayer.sendQueue.getNetworkManager().sendPacketInstantly(event.getPacket());
            }
        }
    };
}
