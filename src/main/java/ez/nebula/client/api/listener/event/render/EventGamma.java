package ez.nebula.client.api.listener.event.render;

import ez.nebula.client.api.listener.Event;

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
