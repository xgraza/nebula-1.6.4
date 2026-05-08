package us.nebula.client.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import us.nebula.client.command.Command;
import us.nebula.client.command.trait.CommandManifest;
import us.nebula.client.command.trait.CommandSource;

@CommandManifest(aliases = { "ping", "latency" })
public final class PingCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.executes((ctx) ->
        {
            return ctx.getSource().respond("Latency is 0ms");
        });
    }
}
