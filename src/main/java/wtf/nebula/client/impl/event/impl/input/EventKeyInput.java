package wtf.nebula.client.impl.event.impl.input;

import me.bush.eventbus.event.Event;

public class EventKeyInput extends Event {
    private final int keyCode;
    private final boolean state;

    public EventKeyInput(int keyCode, boolean state) {
        this.keyCode = keyCode;
        this.state = state;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
