package us.nebula.client.impl.cheat.player;

import us.nebula.client.ClientSettings;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatInstance;
import us.nebula.client.api.manager.cheat.CheatManifest;
import us.nebula.client.api.rpc.DiscordRPCHandler;
import us.nebula.client.util.player.ChatUtil;

/**
 * @author xgraza
 * @since 03/07/25
 */
@CheatManifest(name = "DiscordRPC",
        description = "Shows that you're using Nebula on discord",
        category = CheatCategory.PLAYER)
public final class DiscordRPCCheat extends Cheat
{
    @CheatInstance
    public static DiscordRPCCheat INSTANCE;

    @Override
    public void onEnable()
    {
        super.onEnable();
        if (ClientSettings.VERBOSE_LOGGING)
        {
            ChatUtil.send("Starting DiscordRPCHandler...");
        }
        DiscordRPCHandler.start();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        if (ClientSettings.VERBOSE_LOGGING)
        {
            ChatUtil.send("Stopping DiscordRPCHandler thread & connection...");
        }
        DiscordRPCHandler.stop();
    }
}
