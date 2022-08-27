package wtf.nebula.client.impl.module.miscellaneous;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.gui.GuiGameOver;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AutoRespawn extends ToggleableModule {
    public AutoRespawn() {
        super("Auto Respawn", new String[]{"autorespawn", "respawn"}, ModuleCategory.MISCELLANEOUS);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentScreen instanceof GuiGameOver || mc.thePlayer.isDead || mc.thePlayer.getHealth() <= 0.0f) {
            mc.thePlayer.respawnPlayer();
        }
    }
}
