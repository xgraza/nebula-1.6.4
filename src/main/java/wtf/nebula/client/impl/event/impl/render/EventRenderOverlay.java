package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventRenderOverlay extends Event {
    private final Type type;

    public EventRenderOverlay(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }

    public enum Type {
        HURTCAM, FIRE, BLOCK, CONFUSION
    }
}
