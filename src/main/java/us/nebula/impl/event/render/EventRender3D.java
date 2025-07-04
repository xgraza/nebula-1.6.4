package us.nebula.impl.event.render;

import us.nebula.api.listener.Event;
import us.nebula.util.render.Renderer3D;

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
