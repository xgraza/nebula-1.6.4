package lol.nebula.listener.events.render.world;

import lol.nebula.listener.bus.CancelableEvent;
import net.minecraft.block.Block;

/**
 * @author aesthetical
 * @since 05/16/23
 */
public class EventRenderBlockSide extends CancelableEvent {
    private final Block block;
    private final int x, y, z, face;

    private boolean renderSide;

    public EventRenderBlockSide(Block block, int x, int y, int z, int face) {
        this.block = block;
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
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

    public int getFace() {
        return face;
    }

    public void setRenderSide(boolean renderSide) {
        this.renderSide = renderSide;
    }

    public boolean isRenderSide() {
        return renderSide;
    }
}
