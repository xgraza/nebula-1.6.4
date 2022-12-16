package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;

public class EventRightClickBlock extends Event {
    private final int x, y, z, facing;
    private final Vec3 hitVec;
    private final ItemStack stack;

    public EventRightClickBlock(int x, int y, int z, int facing, Vec3 hitVec, ItemStack stack) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.facing = facing;
        this.hitVec = hitVec;
        this.stack = stack;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getFacing() {
        return facing;
    }

    public Vec3 getHitVec() {
        return hitVec;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
