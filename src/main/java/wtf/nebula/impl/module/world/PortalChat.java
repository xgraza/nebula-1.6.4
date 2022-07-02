package wtf.nebula.impl.module.world;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class PortalChat extends Module {
    public PortalChat() {
        super("PortalChat", ModuleCategory.WORLD);
    }

    @EventListener
    public void onTicK(TickEvent event) {
        mc.thePlayer.inPortal = false;
    }
}
