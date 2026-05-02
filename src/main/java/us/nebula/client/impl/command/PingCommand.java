package us.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import us.nebula.client.api.manager.command.Command;
import us.nebula.client.api.manager.command.CommandManifest;
import us.nebula.client.api.manager.command.CommandSource;

@CommandManifest(aliases = {"ping", "latency"})
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
