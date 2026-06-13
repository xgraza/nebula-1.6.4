package ez.nebula.client.impl.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import ez.nebula.client.api.manager.command.Command;
import ez.nebula.client.api.manager.command.trait.CommandManifest;
import ez.nebula.client.api.manager.command.trait.CommandSource;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.util.io.NetworkUtil;

@CommandManifest(aliases = { "ping", "latency" }, description = "Displays your player latency")
public final class PingCommand extends Command
{
    @Override
    public void createBuilder(LiteralArgumentBuilder<CommandSource> literal)
    {
        literal.executes((ctx) ->
        {
            final int latency = NetworkUtil.getLatency(MC.thePlayer);
            return ctx.getSource().respond("Your ping to the server is %sms", latency);
        });
    }
}
