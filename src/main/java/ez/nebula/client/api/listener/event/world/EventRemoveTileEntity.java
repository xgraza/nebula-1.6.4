package ez.nebula.client.api.listener.event.world;

import net.minecraft.tileentity.TileEntity;
import ez.nebula.client.api.listener.Event;

public final class EventRemoveTileEntity extends Event
{
    private final TileEntity tileEntity;
    private final int x, y, z;

    public EventRemoveTileEntity(TileEntity tileEntity, int x, int y, int z)
    {
        this.tileEntity = tileEntity;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public TileEntity getTileEntity()
    {
        return tileEntity;
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
}
