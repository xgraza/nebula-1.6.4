package wtf.nebula.client.impl.event.impl.world;

import me.bush.eventbus.event.Event;
import net.minecraft.block.BlockLiquid;

public class EventLiquidCollideCheck extends Event {
    private final BlockLiquid blockLiquid;
    private boolean in;

    public EventLiquidCollideCheck(BlockLiquid blockLiquid, boolean in) {
        this.blockLiquid = blockLiquid;
        this.in = in;
    }

    public BlockLiquid getBlockLiquid() {
        return blockLiquid;
    }

    public boolean isIn() {
        return in;
    }

    public void setIn(boolean in) {
        this.in = in;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
