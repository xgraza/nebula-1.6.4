package yzy.szn.impl.event;

import net.minecraft.client.gui.ScaledResolution;
import yzy.szn.api.eventbus.Event;

/**
 * @author xgraza
 * @since 03/12/24
 */
public class EventRender extends Event {

    private final float partialTicks;

    public EventRender(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public final float getPartialTicks() {
        return partialTicks;
    }

    public static final class Overlay extends EventRender {

        private final ScaledResolution resolution;

        public Overlay(float partialTicks, ScaledResolution resolution) {
            super(partialTicks);
            this.resolution = resolution;
        }

        public ScaledResolution getResolution() {
            return resolution;
        }
    }
}
