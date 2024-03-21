package nebula.client.command.impl;

import nebula.client.command.Command;
import nebula.client.command.CommandMeta;
import nebula.client.command.CommandResults;
import nebula.client.util.player.FakePlayerUtils;

/**
 * @author Gavin
 * @since 08/24/23
 */
@SuppressWarnings("unused")
@CommandMeta(aliases = {"fakeplayer", "fake"},
  description = "Spawns a fake player")
public class FakePlayerCommand extends Command {

  private static final int FAKE_PLAYER_ID = -133769;

  @Override
  public int execute(String[] args) throws Exception {
    if (mc.theWorld == null)
      throw new RuntimeException("how the fuck did you do this");

    if (mc.theWorld.getEntityByID(FAKE_PLAYER_ID) == null) {
      FakePlayerUtils.spawn(FAKE_PLAYER_ID, mc.thePlayer);
    } else {
      FakePlayerUtils.despawn(FAKE_PLAYER_ID);
    }

    return CommandResults.SUCCESS;
  }
}
