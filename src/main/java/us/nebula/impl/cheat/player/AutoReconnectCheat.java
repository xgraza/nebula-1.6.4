package us.nebula.impl.cheat.player;

import net.minecraft.client.multiplayer.ServerData;
import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.api.value.Setting;

/**
 * @author xgraza
 * @since 03/30/25
 */
@CheatManifest(name = "AutoReconnect",
        description = "Automatically reconnects you to the last server",
        category = CheatCategory.PLAYER)
public final class AutoReconnectCheat extends Cheat
{
    @CheatInstance
    public static AutoReconnectCheat INSTANCE;

    public final Setting<Integer> delaySetting = new Setting<>(
            "Delay", 5, 1, 20, 1);

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
