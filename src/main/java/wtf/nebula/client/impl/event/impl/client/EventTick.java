package wtf.nebula.client.impl.event.impl.client;

import me.bush.eventbus.event.Event;

public class EventTick extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
