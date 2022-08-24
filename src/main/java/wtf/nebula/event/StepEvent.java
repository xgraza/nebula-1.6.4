package wtf.nebula.event;

import me.bush.eventbus.event.Event;
import net.minecraft.util.AxisAlignedBB;

public class StepEvent extends Event {
    private final AxisAlignedBB box;

    public StepEvent(AxisAlignedBB box) {
        this.box = box;
    }

    public AxisAlignedBB getBox() {
        return box;
    }

    @Override
    protected boolean isCancellable() {
        return false;
    }
}
