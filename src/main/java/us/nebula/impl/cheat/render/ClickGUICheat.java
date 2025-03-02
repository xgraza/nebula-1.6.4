package us.nebula.impl.cheat.render;

import us.nebula.api.manager.cheat.Cheat;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatInstance;
import us.nebula.api.manager.cheat.CheatManifest;
import us.nebula.impl.gui.cheat.ClickGUIScreen;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "ClickGUI", category = CheatCategory.RENDER)
public final class ClickGUICheat extends Cheat
{
    @CheatInstance
    public static ClickGUICheat INSTANCE;

    private ClickGUIScreen guiScreen;

    public ClickGUICheat()
    {
        getKey().setKeyCode(KEY_RSHIFT);
    }

    @Override
    protected void onEnable()
    {
        if (MC.thePlayer == null || MC.theWorld == null)
        {
            toggle();
            return;
        }
        if (guiScreen == null)
        {
            guiScreen = new ClickGUIScreen();
        }
        MC.displayGuiScreen(guiScreen);
        setToggled(false);
    }

    @Override
    protected void onDisable()
    {
        // overriden because i dont want eventbus stuff
    }
}
