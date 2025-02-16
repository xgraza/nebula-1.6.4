package us.nebula.impl.cheat.player;

import net.minecraft.network.play.client.C16PacketClientStatus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.event.player.EventPlayerDeath;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "AutoRespawn", category = CheatCategory.PLAYER)
public final class AutoRespawnCheat extends Cheat
{
    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (event.getPlayer().equals(MC.thePlayer))
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(
                    C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    };
}
