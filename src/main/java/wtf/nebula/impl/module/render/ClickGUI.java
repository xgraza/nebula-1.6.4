package wtf.nebula.impl.module.render;

import org.lwjgl.input.Keyboard;
import wtf.nebula.impl.module.Module;
import wtf.nebula.impl.module.ModuleCategory;
import wtf.nebula.impl.gui.ui.ClickGUIScreen;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", ModuleCategory.RENDER);
        setBind(Keyboard.KEY_RSHIFT);
        drawn.setValue(false);
    }

    @Override
    protected void onActivated() {
        super.onActivated();

        if (nullCheck()) {
            setState(false);
            return;
        }

        mc.displayGuiScreen(ClickGUIScreen.getInstance());
    }
}
