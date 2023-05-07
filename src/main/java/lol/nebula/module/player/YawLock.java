package lol.nebula.module.player;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;

/**
 * @author aesthetical
 * @since 05/07/23
 */
public class YawLock extends Module {
    public YawLock() {
        super("Yaw Lock", "Locks your client yaw", ModuleCategory.PLAYER);
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        mc.thePlayer.rotationYaw = Math.round((mc.thePlayer.rotationYaw + 1.0f) / 45.0f) * 45.0f;
    }
}
