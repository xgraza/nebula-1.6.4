package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.settings.KeyBinding;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class AutoWalk extends Module {
    public AutoWalk() {
        super("AutoWalk", ModuleCategory.MOVEMENT);
    }

    @Override
    protected void onDeactivated() {
        super.onDeactivated();
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, false);
    }

    @EventListener
    public void onTick(TickEvent event) {

        KeyBinding.setKeyBindState(mc.gameSettings.keyBindForward.keyCode, true);
    }
}
