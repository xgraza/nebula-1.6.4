package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D extends Event {
    private final float partialTicks;
    private final ScaledResolution resolution;

    public EventRender2D(float partialTicks, ScaledResolution resolution) {
        this.partialTicks = partialTicks;
        this.resolution = resolution;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public ScaledResolution getResolution() {
        return resolution;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
