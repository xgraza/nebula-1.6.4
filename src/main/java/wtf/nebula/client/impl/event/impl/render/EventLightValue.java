package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;

public class EventLightValue extends Event {
    private final Block block;
    private int lightValue;

    public EventLightValue(Block block, int lightValue) {
        this.block = block;
        this.lightValue = lightValue;
    }

    public Block getBlock() {
        return block;
    }

    public int getLightValue() {
        return lightValue;
    }

    public void setLightValue(int lightValue) {
        this.lightValue = lightValue;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
