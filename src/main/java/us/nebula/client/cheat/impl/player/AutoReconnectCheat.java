package us.nebula.client.cheat.impl.player;

import net.minecraft.client.multiplayer.ServerData;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.setting.Setting;

/**
 * @author xgraza
 * @since 03/30/25
 */
@CheatManifest(name = "AutoReconnect",
        description = "Automatically reconnects you to the last server you were connected to",
        category = CheatCategory.PLAYER)
public final class AutoReconnectCheat extends Cheat
{
    @CheatInstance
    public static AutoReconnectCheat INSTANCE;

    public final Setting<Integer> delaySetting = numberBuilder("Delay", 5)
            .setMin(1)
            .setMax(100)
            .setScale(1)
            .setDescription("The delay in seconds until reconnecting to the previous server")
            .build();

    private ServerData lastServer;

    public void setLastServer(ServerData lastServer)
    {
        this.lastServer = lastServer;
    }

    public ServerData getLastServer()
    {
        return lastServer;
    }
}
