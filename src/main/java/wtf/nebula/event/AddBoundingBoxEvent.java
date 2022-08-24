package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class AddBoundingBoxEvent extends Event {
    private AxisAlignedBB box;
    private final int x, y, z;
    private final Block block;
    private final Entity entity;

    public AddBoundingBoxEvent(AxisAlignedBB box, int x, int y, int z, Block block, Entity entity) {
        this.box = box;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.entity = entity;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public void setBox(AxisAlignedBB box) {
        this.box = box;
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

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
