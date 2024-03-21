package nebula.client.command.impl;

import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 08/10/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"spawn", "spawntp", "stp"},
  description = "Sends you to the server spawn point")
public class SpawnTPCommand extends Command {
  @Override
  public int execute(String[] args) {
    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
      Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));

    return CommandResults.SUCCESS;
  }
}
