package us.nebula.client.impl.event.render;

import us.nebula.client.api.listener.Event;
import us.nebula.client.util.render.Renderer3D;

/**
 * @author xgraza
 * @since 03/02/25
 */
public final class EventRender3D extends Event
{
    private final Renderer3D renderer;
    private final float partialTicks;

    public EventRender3D(final Renderer3D renderer, final float partialTicks)
    {
        this.renderer = renderer;
        this.partialTicks = partialTicks;
    }

    public Renderer3D getRenderer()
    {
        return renderer;
    }

    public float getPartialTicks()
    {
        return partialTicks;
    }
}
