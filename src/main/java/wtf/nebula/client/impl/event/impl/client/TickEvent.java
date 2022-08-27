package wtf.nebula.client.impl.event.impl.client;

import me.bush.eventbus.event.Event;

public class TickEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
