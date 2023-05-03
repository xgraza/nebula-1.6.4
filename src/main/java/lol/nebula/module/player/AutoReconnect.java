package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.world.EventServerChange;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import net.minecraft.client.multiplayer.ServerData;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class AutoReconnect extends Module {

    private ServerData serverData;

    public final Setting<Double> delay = new Setting<>(1.0, 0.01, 0.0, 10.0, "Delay");

    public AutoReconnect() {
        super("Auto Reconnect", "Automatically reconnects to the last server you were on", ModuleCategory.PLAYER);
    }

    @Listener
    public void onServerChange(EventServerChange event) {
        if (event.getServerData() != null) serverData = event.getServerData();
    }

    public ServerData getServerData() {
        return serverData;
    }
}
