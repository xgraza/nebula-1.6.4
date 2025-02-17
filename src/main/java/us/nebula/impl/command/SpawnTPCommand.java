package us.nebula.impl.command;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.api.manager.command.Command;
import us.nebula.api.manager.command.CommandManifest;
import us.nebula.api.manager.command.CommandResult;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CommandManifest(aliases = {"spawntp", "spawntp", "stp"})
public final class SpawnTPCommand extends Command
{
    @Override
    public void build()
    {
        argumentBuilder.dispatchSingle(() ->
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
            return CommandResult.SUCCESS_DEFAULT;
        });
    }
}
