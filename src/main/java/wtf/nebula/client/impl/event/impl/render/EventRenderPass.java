package wtf.nebula.client.impl.event.impl.render;

import me.bush.eventbus.event.Event;
import net.minecraft.block.Block;

public class EventRenderPass extends Event {
    private final Block block;
    private int renderPass;

    public EventRenderPass(Block block, int renderPass) {
        this.block = block;
        this.renderPass = renderPass;
    }

    public Block getBlock() {
        return block;
    }

    public int getRenderPass() {
        return renderPass;
    }

    public void setRenderPass(int renderPass) {
        this.renderPass = renderPass;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
