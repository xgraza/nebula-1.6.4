package wtf.nebula.client.impl.event.impl.move;

import me.bush.eventbus.event.Event;

public class EventSafeWalk extends Event {
    @Override
    protected boolean isCancellable() {
        return true;
    }
}
