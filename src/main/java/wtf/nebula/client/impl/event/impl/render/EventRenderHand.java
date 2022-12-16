package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;

public class EventRenderHand extends Event {
    private final float partialTicks;
    private final int wtf;

    public EventRenderHand(float partialTicks, int wtf) {
        this.partialTicks = partialTicks;
        this.wtf = wtf;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getWtf() {
        return wtf;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
