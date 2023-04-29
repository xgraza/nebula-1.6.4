package lol.nebula.module.movement;

import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

/**
 * @author aesthetical
 * @since 04/29/23
 */
public class InvWalk extends Module {
    private KeyBinding[] binds;

    public InvWalk() {
        super("Inv Walk", "Allows you to walk inside of inventories", ModuleCategory.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        binds = null;
    }

    @Listener
    public void onUpdate(EventUpdate event) {

        // do not try to move in these screens
        if (mc.currentScreen == null
                || mc.currentScreen instanceof GuiChat
                || mc.currentScreen instanceof GuiRepair
                || mc.currentScreen instanceof GuiContainerCreative) return;

        if (binds == null) {
            binds = new KeyBinding[] {
                    mc.gameSettings.keyBindForward,
                    mc.gameSettings.keyBindLeft,
                    mc.gameSettings.keyBindRight,
                    mc.gameSettings.keyBindBack };
        }

        for (KeyBinding keyBinding : binds) {
            int keyCode = keyBinding.getKeyCode();
            if (keyCode > 0) {
                keyBinding.pressed = Keyboard.isKeyDown(keyCode);
            } else {
                keyBinding.pressed = Mouse.isButtonDown(100 + keyCode);
            }
        }
    }
}
