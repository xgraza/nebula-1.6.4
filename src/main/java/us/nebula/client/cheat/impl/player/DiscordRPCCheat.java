package us.nebula.client.cheat.impl.player;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.rpc.DiscordRPCHandler;

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
        DiscordRPCHandler.start();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
        DiscordRPCHandler.stop();
    }
}
