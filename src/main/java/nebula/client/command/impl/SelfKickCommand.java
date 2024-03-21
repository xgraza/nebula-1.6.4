package nebula.client.command.impl;

import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import net.minecraft.network.play.client.C03PacketPlayer;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"selfkick", "skick"},
  description = "Kicks yourself from the server you're on")
public class SelfKickCommand extends Command {
  @Override
  public int execute(String[] args) throws Exception {

    mc.thePlayer.sendQueue.addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(
      mc.thePlayer.posX,
      mc.thePlayer.boundingBox.minY,
      mc.thePlayer.posY + 0.1,
      mc.thePlayer.posZ,
      mc.thePlayer.onGround));

    return CommandResults.SUCCESS;
  }
}
