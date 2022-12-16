package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventAlphaMultiplier extends Event {
    public int a;

    public EventAlphaMultiplier(int a) {
        this.a = a;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
