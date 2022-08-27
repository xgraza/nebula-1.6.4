package wtf.nebula.client.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import wtf.nebula.client.impl.event.impl.client.TickEvent;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;

public class AutoWalk extends ToggleableModule {
    public AutoWalk() {
        super("Auto Walk", new String[]{"autowalk", "autowalker", "whold"}, ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        mc.gameSettings.keyBindForward.pressed = false;
    }

    @EventListener
    public void onTick(TickEvent event) {
        mc.gameSettings.keyBindForward.pressed = true;
    }
}
