package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.SafewalkEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class Safewalk extends Module {
    public Safewalk() {
        super("Safewalk", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onSafewalk(SafewalkEvent event) {
        event.setCancelled(true);
    }
}
