package us.nebula.client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import us.nebula.client.command.Command;
import us.nebula.client.command.trait.CommandManifest;
import us.nebula.client.command.trait.CommandSource;

@CommandManifest(aliases = {"selfkick", "sf"}, description = "Kicks yourself from the server")
public final class SelfKickCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.executes((ctx) ->
        {
            MC.theWorld.sendQuittingDisconnectingPacket();
            return ctx.getSource().respond("Done");
        });
    }
}
