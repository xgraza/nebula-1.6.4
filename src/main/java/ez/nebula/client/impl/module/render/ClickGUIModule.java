package ez.nebula.client.impl.module.render;

import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.impl.gui.module.ClickGUIScreen;
import ez.nebula.client.api.setting.Setting;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author xgraza
 * @since 02/16/25
 */
@ModuleManifest(name = "ClickGUI",
        description = "Displays a GUI with an Overview of all cheats & their settings",
        category = ModuleCategory.RENDER)
public final class ClickGUIModule extends Module
{
    @ModuleInstance
    public static ClickGUIModule INSTANCE;

    public final Setting<Boolean> saveOnCloseSetting = builder("Save on Close", true)
            .setDescription("If to save your cheat config every time the ClickGUi is closed")
            .build();
    public final Setting<Boolean> hoverDescriptionSetting = builder("Hover Description", true)
            .setDescription("If to show descriptions when hovering over an item")
            .build();

    private ClickGUIScreen guiScreen;

    public ClickGUIModule()
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
