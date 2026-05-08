package us.nebula.client.listener.event.render;

import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 02/16/25
 */
public final class EventGamma extends Event
{
    private float gamma;

    public EventGamma(final float gamma)
    {
        this.gamma = gamma;
    }

    public float getGamma()
    {
        return gamma;
    }

    public void setGamma(float gamma)
    {
        this.gamma = gamma;
    }
}
