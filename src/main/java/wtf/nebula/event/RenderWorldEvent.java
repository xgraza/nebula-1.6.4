package wtf.nebula.event;

import me.bush.eventbus.event.Event;

public class RenderWorldEvent extends Event {
    private final float partialTicks;

    public RenderWorldEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
