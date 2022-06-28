package wtf.nebula.impl.module.movement;

import me.bush.eventbus.annotation.EventListener;
import net.minecraft.src.GuiChat;
import net.minecraft.src.KeyBinding;
import org.lwjgl.input.Keyboard;
import wtf.nebula.event.TickEvent;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;

public class InventoryMove extends Module {
    private static final KeyBinding[] BINDS = new KeyBinding[] {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindJump
    };

    public InventoryMove() {
        super("InventoryMove", ModuleCategory.MOVEMENT);
    }

    @EventListener
    public void onTick(TickEvent event) {
        if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat)) {

            for (KeyBinding binding : BINDS) {
                KeyBinding.setKeyBindState(binding.keyCode, Keyboard.isKeyDown(binding.keyCode));
            }
        }
    }
}
