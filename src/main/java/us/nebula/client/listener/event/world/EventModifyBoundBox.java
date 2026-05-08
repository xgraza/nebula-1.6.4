package us.nebula.client.listener.event.world;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import us.nebula.client.listener.Event;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class EventModifyBoundBox extends Event
{
    private final int x, y, z;
    private final Entity entity;
    private final World world;
    private AxisAlignedBB aabb;

    public EventModifyBoundBox(int x, int y, int z, Entity entity, World world, AxisAlignedBB aabb)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.entity = entity;
        this.world = world;
        this.aabb = aabb;
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

    public Entity getEntity()
    {
        return entity;
    }

    public World getWorld()
    {
        return world;
    }

    public AxisAlignedBB getAabb()
    {
        return aabb;
    }

    public void setAabb(AxisAlignedBB aabb)
    {
        this.aabb = aabb;
    }
}
