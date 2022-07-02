package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.PushOutOfBlocksEvent;
import wtf.nebula.event.WaterMovementEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.value.Value;

public class NoPush extends Module {
    public NoPush() {
        super("NoPush", ModuleCategory.MOVEMENT);
    }

    public final Value<Boolean> blocks = new Value<>("Blocks", true);
//    public final Value<Boolean> liquid = new Value<>("Liquid", true);

    @EventListener
    public void onBlockPush(PushOutOfBlocksEvent event) {
        event.setCancelled(blocks.getValue());
    }

//    @EventListener
//    public void onWaterMovement(WaterMovementEvent event) {
//        event.setCancelled(liquid.getValue());
//    }
}
