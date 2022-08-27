package wtf.nebula.client.impl.module.active;

import org.lwjgl.input.Keyboard;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.impl.module.active.clickgui.ClickGUIScreen;

public class ClickGUI extends ToggleableModule {
    public ClickGUI() {
        super("Click UI", new String[]{"clickgui", "clickui", "nagivation"}, ModuleCategory.ACTIVE);
        setBind(Keyboard.KEY_RSHIFT);
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (isNull()) {
            setRunning(false);
        } else {
            mc.displayGuiScreen(ClickGUIScreen.getInstance());
        }
    }
}
