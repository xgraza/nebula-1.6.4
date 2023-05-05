package lol.nebula.listener.events.render.gui.overlay;

import net.minecraft.client.gui.ScaledResolution;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class EventRender2D {

    /**
     * The scaled screen resolution
     */
    private final ScaledResolution res;

    /**
     * The render partial ticks
     */
    private final float partialTicks;

    public EventRender2D(ScaledResolution res, float partialTicks) {
        this.res = res;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getRes() {
        return res;
    }

    public float getPartialTicks() {
        return partialTicks;
    }
}
