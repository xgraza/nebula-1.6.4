package us.nebula.impl.event.player;

import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 02/20/25
 */
public final class EventStep extends Event
{
    private float stepHeight;

    public EventStep(final float stepHeight)
    {
        this.stepHeight = stepHeight;
    }

    public float getStepHeight()
    {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight)
    {
        this.stepHeight = stepHeight;
    }
}
