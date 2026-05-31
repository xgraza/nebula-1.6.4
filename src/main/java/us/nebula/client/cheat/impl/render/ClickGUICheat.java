package us.nebula.client.cheat.impl.render;

import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.trait.CheatInstance;
import us.nebula.client.cheat.trait.CheatManifest;
import us.nebula.client.cheat.gui.ClickGUIScreen;
import us.nebula.client.setting.Setting;

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

    public final Setting<Boolean> saveOnCloseSetting = builder("Save on Close", true)
            .setDescription("If to save your cheat config every time the ClickGUi is closed")
            .build();
    public final Setting<Boolean> hoverDescriptionSetting = builder("Hover Description", true)
            .setDescription("If to show descriptions when hovering over an item")
            .build();

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

    public void resetClickGUI()
    {
        MC.displayGuiScreen(null);
        guiScreen = null;
        MC.displayGuiScreen(guiScreen = new ClickGUIScreen());
        notifyInfo("Successfully reset ClickGUI", 5000L);
    }
}
