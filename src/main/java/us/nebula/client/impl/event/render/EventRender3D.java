package us.nebula.client.impl.event.render;

import us.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EventRender3D extends Event
{
    private final float partialTicks;

    public EventRender3D(final float partialTicks)
    {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
