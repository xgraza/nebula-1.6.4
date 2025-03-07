package us.nebula.impl.cheat.player;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.rpc.DiscordRPCHandler;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "DiscordRPC",
        description = "Shows that you're using Nebula on discord (if it's on)",
        category = CheatCategory.PLAYER)
public final class DiscordRPCCheat extends Cheat
{
    @Override
    protected void onEnable()
    {
        super.onEnable();
        DiscordRPCHandler.start();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        DiscordRPCHandler.stop();
    }
}
