package us.nebula.impl.cheat.player;

import us.nebula.ClientSettings;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.rpc.DiscordRPCHandler;
import us.nebula.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "DiscordRPC",
        description = "Shows that you're using Nebula on discord",
        category = CheatCategory.PLAYER)
public final class DiscordRPCCheat extends Cheat
{
    @Override
    protected void onEnable()
    {
        super.onEnable();
        if (ClientSettings.VERBOSE_LOGGING)
        {
            ChatUtil.send("Starting DiscordRPCHandler...");
        }
        DiscordRPCHandler.start();
    }

    @Override
    protected void onDisable()
    {
        super.onDisable();
        if (ClientSettings.VERBOSE_LOGGING)
        {
            ChatUtil.send("Stopping DiscordRPCHandler thread & connection...");
        }
        DiscordRPCHandler.stop();
    }
}
