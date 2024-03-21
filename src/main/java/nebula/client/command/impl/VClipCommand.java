package nebula.client.command.impl;

import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;

/**
 * @author Gavin
 * @since 08/10/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"vclip"},
  description = "Clips into the ground",
  syntax = "<blocks>")
public class VClipCommand extends Command {
  @Override
  public int execute(String[] args) {
    if (args.length == 0) return CommandResults.INVALID_SYNTAX;

    double value;
    try {
      value = Double.parseDouble(args[0]);
    } catch (NumberFormatException e) {
      return CommandResults.INVALID_SYNTAX;
    }

    mc.thePlayer.setPosition(mc.thePlayer.posX,
      mc.thePlayer.boundingBox.minY + value, mc.thePlayer.posZ);

    return CommandResults.SUCCESS;
  }
}
