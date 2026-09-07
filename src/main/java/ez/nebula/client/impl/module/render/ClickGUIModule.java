package ez.nebula.client.impl.module.render;

import com.google.gson.JsonElement;
import ez.nebula.client.api.manager.module.Module;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.api.manager.module.trait.ModuleInstance;
import ez.nebula.client.api.manager.module.trait.ModuleManifest;
import ez.nebula.client.impl.gui.clickgui.ClickGUIScreen;
import ez.nebula.client.api.setting.Setting;
import net.minecraft.client.gui.GuiMainMenu;

import static org.lwjgl.input.Keyboard.KEY_RSHIFT;

/**
 * @author xgraza
 * @since 02/16/25
 */
@ModuleManifest(name = "ClickGUI",
        description = "Displays a panel-styled GUI that displays all available modules and their configurations",
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
    public final Setting<Double> guiScaleSetting = numberBuilder("GUI Scale", 1.0)
            .setMin(0.5)
            .setMax(2.0)
            .setScale(0.1)
            .setDescription("The scale setting")
            .build();
    public final Setting<Double> scrollSpeedSetting = numberBuilder("Scroll Speed", 0.15)
            .setMin(0.1)
            .setMax(1.0)
            .setScale(0.05)
            .setDescription("The speed that smooth scrolling should have")
            .build();

    private ClickGUIScreen guiScreen;

    public ClickGUIModule()
    {
        getKey().setKeyCode(KEY_RSHIFT);
    }

    @Override
    public void onEnable()
    {
        if (!canShowClickGUI())
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

    private boolean canShowClickGUI()
    {
        if (MC.currentScreen instanceof GuiMainMenu)
        {
            return isToggled();
        }
        return MC.thePlayer != null && MC.theWorld != null;
    }

    public void resetClickGUI()
    {
        MC.displayGuiScreen(null);
        guiScreen = null;
        MC.displayGuiScreen(guiScreen = new ClickGUIScreen());
        notifyInfo("Successfully reset ClickGUI", 5000L);
    }

    @Override
    public void fromJSON(JsonElement element)
    {
        super.fromJSON(element);
        if (isToggled() && MC.theWorld == null && MC.thePlayer == null)
        {
            setToggled(false);
        }
    }
}
