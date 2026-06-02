package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;

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
