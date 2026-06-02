package ez.nebula.client.api.listener.event.player;

import ez.nebula.client.api.listener.Event;

/**
 * @author xgraza
 * @since 03/01/25
 */
public class EventMoveUpdate extends Event
{
    private double x, y, stance, z;
    private float yaw, pitch;
    private boolean onGround;
    private double minMove = 9.0E-4D;

    public EventMoveUpdate(double x, double y, double stance, double z, float yaw, float pitch, boolean onGround)
    {
        this.x = x;
        this.y = y;
        this.stance = stance;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
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

    public double getStance()
    {
        return stance;
    }

    public void setStance(double stance)
    {
        this.stance = stance;
    }

    public double getZ()
    {
        return z;
    }

    public void setZ(double z)
    {
        this.z = z;
    }

    public float getYaw()
    {
        return yaw;
    }

    public void setYaw(float yaw)
    {
        this.yaw = yaw;
    }

    public float getPitch()
    {
        return pitch;
    }

    public void setPitch(float pitch)
    {
        this.pitch = pitch;
    }

    public boolean isOnGround()
    {
        return onGround;
    }

    public void setOnGround(boolean onGround)
    {
        this.onGround = onGround;
    }

    public void setMinMove(double minMove)
    {
        this.minMove = minMove;
    }

    public double getMinMove()
    {
        return minMove;
    }

    public static final class Post extends EventMoveUpdate
    {
        public Post(double x, double y, double stance, double z, float yaw, float pitch, boolean onGround)
        {
            super(x, y, stance, z, yaw, pitch, onGround);
        }
    }
}
