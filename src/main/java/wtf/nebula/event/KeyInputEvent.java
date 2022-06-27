package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class KeyInputEvent extends Event {
    private final int keyCode;
    private final boolean state;

    public KeyInputEvent(int keyCode, boolean state) {
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
