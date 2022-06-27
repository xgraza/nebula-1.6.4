package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class TickEvent extends Event {
    @Override
    protected boolean isCancellable() {
        return false;
    }
}
