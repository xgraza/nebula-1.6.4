package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;

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
