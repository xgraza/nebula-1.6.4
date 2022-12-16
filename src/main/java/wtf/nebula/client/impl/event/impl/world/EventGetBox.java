package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;

public class EventGetBox extends Event {
    private final Block block;
    private final int x, y, z;
    private AxisAlignedBB box;

    public EventGetBox(Block block, int x, int y, int z, AxisAlignedBB box) {
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
        this.box = box;
    }

    public Block getBlock() {
        return block;
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

    public AxisAlignedBB getBox() {
        return box;
    }

    public void setBox(AxisAlignedBB box) {
        this.box = box;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
