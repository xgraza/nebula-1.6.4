package us.nebula.impl.cheat.player;

import net.minecraft.network.play.client.C16PacketClientStatus;
import us.nebula.api.listener.EventListener;
import us.nebula.api.listener.Subscribe;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;
import us.nebula.impl.event.player.EventPlayerDeath;
import us.nebula.util.ChatUtil;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "AutoRespawn",
        description = "Automatically respawns & retains coordinates of death",
        category = CheatCategory.PLAYER)
public final class AutoRespawnCheat extends Cheat
{
    private final Setting<Boolean> logCoordsSetting = new Setting<>("Log Coordinates", false);

    @Subscribe
    private final EventListener<EventPlayerDeath> playerDeathEventListener = event ->
    {
        if (event.getPlayer().equals(MC.thePlayer))
        {
            if (logCoordsSetting.getValue())
            {
                // TODO: save to file...
                ChatUtil.send("Saving coordinates at XYZ: %1.f, %1.f, %1.f", MC.thePlayer.posX, MC.thePlayer.boundingBox.minY, MC.thePlayer.posZ);
            }
            MC.thePlayer.sendQueue.addToSendQueue(new C16PacketClientStatus(
                    C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
        }
    };
}
