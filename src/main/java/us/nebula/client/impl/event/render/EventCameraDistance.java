package us.nebula.client.impl.event.render;

import us.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 03/06/25
 */
public final class EventCameraDistance extends Event
{
    private double cameraDistance;

    public EventCameraDistance(final double cameraDistance)
    {
        this.cameraDistance = cameraDistance;
    }

    public void setCameraDistance(double cameraDistance)
    {
        this.cameraDistance = cameraDistance;
    }

    public double getCameraDistance()
    {
        return cameraDistance;
    }
}
