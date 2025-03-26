package us.nebula.impl.event.player;

import us.nebula.api.listener.Event;

/**
 * @author xgraza
 * @since 03/26/25
 */
public final class EventAttackBlock extends Event
{
    private final int x, y, z, side;

    public EventAttackBlock(final int x, final int y, final int z, final int side)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public int getSide()
    {
        return side;
    }
}
