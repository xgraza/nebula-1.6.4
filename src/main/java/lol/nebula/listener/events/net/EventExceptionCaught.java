package lol.nebula.listener.events.net;

import lol.nebula.listener.bus.CancelableEvent;

/**
 * @author aesthetical
 * @since 05/27/23
 */
public class EventExceptionCaught extends CancelableEvent {
    private final Throwable throwable;

    public EventExceptionCaught(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
