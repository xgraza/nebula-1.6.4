package yzy.szn.impl.module.render;

import org.lwjgl.input.Keyboard;
import yzy.szn.api.module.Module;
import yzy.szn.api.module.ModuleCategory;
import yzy.szn.api.module.ModuleManifest;
import yzy.szn.impl.gui.clickgui.ClickGUIScreen;

import static org.lwjgl.input.Keyboard.KEY_R;
import static org.lwjgl.input.Keyboard.KEY_RSHIFT;
import static yzy.szn.api.MinecraftInstance.mc;

/**
 * @author graza
 * @since 02/18/24
 */
@ModuleManifest(
        name = "ClickGUI",
        category = ModuleCategory.RENDER)
public final class ModuleClickGUI extends Module {

    private ClickGUIScreen screen;

    public ModuleClickGUI() {
        setKeyCode(KEY_RSHIFT);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (mc.thePlayer != null) {

            if (screen == null || Keyboard.isKeyDown(KEY_R)) {
                screen = new ClickGUIScreen(this);
            }

            mc.displayGuiScreen(screen);
        }
    }
}
