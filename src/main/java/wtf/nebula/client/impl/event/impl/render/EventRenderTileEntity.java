package wtf.nebula.client.impl.event.impl.render;

import net.minecraft.tileentity.TileEntity;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.base.EventEraed;

public class EventRenderTileEntity extends EventEraed {
    private final TileEntity tileEntity;

    public EventRenderTileEntity(Era era, TileEntity tileEntity) {
        super(era);
        this.tileEntity = tileEntity;
    }

    public TileEntity getTileEntity() {
        return tileEntity;
    }

    @Override
    protected boolean isCancellable() {
        return true;
    }
}
