package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

public class CollisionBoxEvent extends Event {
    private AxisAlignedBB box;
    private final Block block;
    private final Vec3 vec;

    public CollisionBoxEvent(AxisAlignedBB box, Block block, Vec3 vec) {
        this.box = box;
        this.block = block;
        this.vec = vec;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    public void setBox(AxisAlignedBB box) {
        this.box = box;
    }

    public Block getBlock() {
        return block;
    }

    public Vec3 getVec() {
        return vec;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
