package ez.nebula.client.api.listener.event.world;

import ez.nebula.client.api.listener.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class EventPlace extends Event
{
    private final int x, y, z, side;
    private final ItemStack itemStack;
    private final Vec3 hitVec;

    public EventPlace(int x, int y, int z, int side, ItemStack itemStack, Vec3 hitVec)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.side = side;
        this.itemStack = itemStack;
        this.hitVec = hitVec;
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

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public Vec3 getHitVec()
    {
        return hitVec;
    }
}
