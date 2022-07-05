package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class FastPlace extends Module {
    public FastPlace() {
        super("FastPlace", ModuleCategory.WORLD);
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.rightClickDelayTimer = 0;
    }
}
