package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.Block;
import net.minecraft.src.Entity;

public class AddBoundingBoxEvent extends Event {
    private AxisAlignedBB axisAlignedBB;

    private final Block block;
    private final Entity entity;

    public AddBoundingBoxEvent(AxisAlignedBB axisAlignedBB, Block block, Entity entity) {
        this.axisAlignedBB = axisAlignedBB;
        this.block = block;
        this.entity = entity;
    }

    public AxisAlignedBB getAxisAlignedBB() {
        return axisAlignedBB;
    }

    public void setAxisAlignedBB(AxisAlignedBB axisAlignedBB) {
        this.axisAlignedBB = axisAlignedBB;
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
