package yzy.szn.impl.event;

import yzy.szn.api.eventbus.Event;

/**
 * @author graza
 * @since 02/17/24
 */
public final class EventKey extends Event {
    private final int keyCode;

    public EventKey(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
