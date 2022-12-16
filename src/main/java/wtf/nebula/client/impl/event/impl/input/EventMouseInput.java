package wtf.nebula.client.impl.event.impl.input;

import me.bush.eventbus.event.Event;

public class EventMouseInput extends Event {
    private final int button;
    private final boolean state;

    public EventMouseInput(int button, boolean state) {
        this.button = button;
        this.state = state;
    }

    public int getButton() {
        return button;
    }

    public boolean isState() {
        return state;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
