package wtf.nebula.client.impl.event.impl.network;

import me.bush.eventbus.event.Event;
import net.minecraft.client.multiplayer.ServerData;

public class EventServerJoin extends Event {
    private final ServerData serverData;

    public EventServerJoin(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return serverData;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
