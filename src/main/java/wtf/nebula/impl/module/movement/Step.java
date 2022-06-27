package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Step extends Module {
    public Step() {
        super("Step", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onStep() {

    }
}
