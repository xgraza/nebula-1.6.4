package lol.nebula.listener.events.world;

import net.minecraft.client.multiplayer.ServerData;

/**
 * @author aesthetical
 * @since 05/03/23
 */
public class EventServerChange {
    private final ServerData serverData;

    public EventServerChange(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }
}
