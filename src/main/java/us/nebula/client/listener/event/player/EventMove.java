package us.nebula.client.listener.event.player;

import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 03/17/25
 */
public final class EventMove extends Event
{
    private double x, y, z;

    public EventMove(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return x;
    }

    public void setX(double x)
    {
        this.x = x;
    }

    public double getY()
    {
        return y;
    }

    public void setY(double y)
    {
        this.y = y;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }
}
