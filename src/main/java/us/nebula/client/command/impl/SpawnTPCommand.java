package us.nebula.client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.network.play.client.C03PacketPlayer;
import us.nebula.client.command.Command;
import us.nebula.client.command.trait.CommandManifest;
import us.nebula.client.command.trait.CommandSource;

@CommandManifest(aliases = { "spawntp", "spawn", "stp" })
public final class SpawnTPCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.executes((ctx) ->
        {
            MC.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(
                    Double.NaN, Double.NaN, Double.NaN, Double.NaN, false));
            return ctx.getSource().respond("Sent invalid packet");
        });
    }
}
