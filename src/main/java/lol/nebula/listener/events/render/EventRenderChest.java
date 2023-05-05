package lol.nebula.listener.events.render;

import lol.nebula.listener.events.EventStage;
import net.minecraft.tileentity.TileEntity;

/**
 * @author aesthetical
 * @since 05/04/23
 */
public class EventRenderChest {

    private final EventStage stage;
    private final TileEntity tileEntity;

    public EventRenderChest(EventStage stage, TileEntity tileEntity) {
        this.stage = stage;
        this.tileEntity = tileEntity;
    }

    public EventStage getStage() {
        return stage;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }
}
