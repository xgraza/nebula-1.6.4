package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.util.value.Setting;
import us.nebula.client.cheat.gui.ClickGUIScreen;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author xgraza
 * @since 02/16/25
 */
@CheatManifest(name = "ClickGUI",
        description = "Displays a GUI with an Overview of all cheats & their settings",
        category = CheatCategory.RENDER)
public final class ClickGUICheat extends Cheat
{
    @CheatInstance
    public static ClickGUICheat INSTANCE;

    public final Setting<Boolean> saveOnCloseSetting = new Setting<>(
            "Save on Close", true);
    public final Setting<Boolean> hoverDescriptionSetting = new Setting<>(
            "Hover Description", true);

    private ClickGUIScreen guiScreen;

    public ClickGUICheat()
    {
        getKey().setKeyCode(KEY_RSHIFT);
    }

    @Override
    public void onEnable()
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
    public void onDisable()
    {
        // overriden because i dont want eventbus stuff
    }
}
