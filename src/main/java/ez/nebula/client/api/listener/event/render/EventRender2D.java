package ez.nebula.client.api.listener.event.render;

import ez.nebula.client.api.listener.Event;
import net.minecraft.client.gui.ScaledResolution;

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
