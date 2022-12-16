package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;

public class EventBlockSide extends Event {
    private final Block block;

    public EventBlockSide(Block block) {
        this.block = block;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
