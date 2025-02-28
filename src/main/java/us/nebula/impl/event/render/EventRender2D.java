package us.nebula.impl.event.render;

import net.minecraft.client.gui.ScaledResolution;
import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 02/24/25
 */
public final class EventRender2D extends Event
{
    private final ScaledResolution resolution;
    private final float partialTicks;

    public EventRender2D(ScaledResolution resolution, float partialTicks)
    {
        this.resolution = resolution;
        this.partialTicks = partialTicks;
    }

    public ScaledResolution getResolution()
    {
        return resolution;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
