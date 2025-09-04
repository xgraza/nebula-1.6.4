package us.nebula.impl.command_test;

import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.api.manager.command_test.Command;
import us.nebula.api.manager.command_test.CommandManifest;
import world.xgraza.xcmd.executor.CommandResult;
import world.xgraza.xcmd.parser.CommandContext;

/**
 * @author xgraza
 * @since 08/12/25
 */
@CommandManifest(
        aliases = {"spawntp", "spawn", "stp"},
        description = "Teleports you to the spawn point in the dimension you're in")
public final class SpawnTPCommand extends Command
{
    @Override
    public CommandResult dispatch(final CommandContext ctx)
    {
        MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
        return ctx.ok("Sent spawn packet");
    }
}
