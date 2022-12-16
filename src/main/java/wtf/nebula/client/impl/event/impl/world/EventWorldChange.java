package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public class EventWorldChange extends Event {
    private final WorldClient previous, current;

    public EventWorldChange(WorldClient previous, WorldClient current) {
        this.previous = previous;
        this.current = current;
    }

    public WorldClient getPrevious() {
        return previous;
    }

    public WorldClient getCurrent() {
        return current;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
