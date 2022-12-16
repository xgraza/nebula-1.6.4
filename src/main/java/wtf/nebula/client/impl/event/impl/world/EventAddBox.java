package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventAddBox extends Event {
    private final Block block;
    private final Entity entity;
    private final int x, y, z;
    private AxisAlignedBB box;

    public EventAddBox(Block block, Entity entity, int x, int y, int z, AxisAlignedBB box) {
        this.block = block;
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.box = box;
    }

    public Block getBlock() {
        return block;
    }

    public Entity getEntity() {
        return entity;
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
