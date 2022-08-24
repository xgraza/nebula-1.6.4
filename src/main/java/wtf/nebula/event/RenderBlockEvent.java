package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;
import net.minecraft.util.Vec3;

public class RenderBlockEvent extends Event {
    private final Block block;
    private final Vec3 pos;

    public RenderBlockEvent(Block block, Vec3 pos) {
        this.block = block;
        this.pos = pos;
    }

    public Block getBlock() {
        return block;
    }

    public Vec3 getPos() {
        return pos;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
