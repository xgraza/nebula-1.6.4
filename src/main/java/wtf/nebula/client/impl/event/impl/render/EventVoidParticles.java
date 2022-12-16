package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventVoidParticles extends Event {
    public boolean has;

    public EventVoidParticles(boolean has) {
        this.has = has;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
