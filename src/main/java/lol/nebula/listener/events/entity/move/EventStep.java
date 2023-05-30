package lol.nebula.listener.events.entity.move;

import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public class EventStep {
    private final Entity entity;
    private final AxisAlignedBB box;

    public EventStep(Entity entity, AxisAlignedBB box) {
        this.entity = entity;
        this.box = box;
    }

    public Entity getEntity() {
        return entity;
    }

    public AxisAlignedBB getBox() {
        return box;
    }
}
