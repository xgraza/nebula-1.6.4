package ez.nebula.client.impl.module.player;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.setting.NumberSetting;
import net.minecraft.client.multiplayer.ServerData;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.api.setting.Setting;

/**
 * @author xgraza
 * @since 03/30/25
 */
@ModuleManifest(name = "AutoReconnect",
        description = "Automatically reconnects you to the last server you were connected to",
        category = ModuleCategory.PLAYER)
public final class AutoReconnectModule extends Module
{
    @ModuleInstance
    public static AutoReconnectModule INSTANCE;

    public final NumberSetting<Integer> delaySetting = numberBuilder("Delay", 5)
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
