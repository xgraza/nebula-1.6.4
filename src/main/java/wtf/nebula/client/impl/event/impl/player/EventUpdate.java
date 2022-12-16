package wtf.nebula.client.impl.event.impl.player;

import me.bush.eventbus.event.Event;

public class EventUpdate extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
