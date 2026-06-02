package ez.nebula.client.api.listener.event.world;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public final class EventModifySelectedBoundBox extends EventModifyBoundBox
{
    public EventModifySelectedBoundBox(int x, int y, int z, World world, AxisAlignedBB aabb)
    {
        super(x, y, z, null, world, aabb);
    }
}
