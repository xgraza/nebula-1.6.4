package lol.nebula.module.visual;

import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.ui.click.ClickUIScreen;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author aesthetical
 * @since 04/27/23
 */
public class ClickUI extends Module {

    /**
     * The instance of the Click UI
     */
    private static ClickUIScreen clickUIScreen;

    public final Setting<Boolean> showDescriptions = new Setting<>(true, "Show Descriptions");
    public final Setting<Boolean> pause = new Setting<>(false, "Pause");

    public ClickUI() {
        super("Click UI", "Displays an interface to modify modules and settings", ModuleCategory.VISUAL);

        // set default bind to right shift
        getBind().setKey(KEY_RSHIFT);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        // automatically disable the module
        setState(false);

        // do not try to display the gui screen if we are not in the world
        if (mc.thePlayer == null || mc.theWorld == null) return;

        // create new instance and display gui screen
        if (clickUIScreen == null) clickUIScreen = new ClickUIScreen();
        mc.displayGuiScreen(clickUIScreen);
    }
}
