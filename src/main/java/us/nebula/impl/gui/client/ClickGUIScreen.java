package us.nebula.impl.gui.client;

import net.minecraft.client.gui.GuiScreen;
import us.nebula.Nebula;
import us.nebula.api.manager.cheat.CheatCategory;
import us.nebula.api.manager.cheat.CheatConfig;
import us.nebula.impl.cheat.render.ClickGUICheat;
import us.nebula.impl.gui.client.component.CategoryPanel;
import us.nebula.impl.gui.client.component.cheat.CheatCategoryPanel;
import us.nebula.impl.gui.client.component.config.ConfigCategoryPanel;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ClickGUIScreen extends GuiScreen
{
    public static boolean ALLOW_EXIT_ON_ESC = true;
    public static double MAX_PANEL_HEIGHT;

    private final List<CategoryPanel> categoryPanels = new LinkedList<>();

    public ClickGUIScreen()
    {
        double posX = 16.0;
        for (final CheatCategory category : CheatCategory.values())
        {
            final CheatCategoryPanel panel = new CheatCategoryPanel(category);
            panel.setX(posX);
            panel.setY(26.0);
            posX += panel.getWidth() + 5;
            categoryPanels.add(panel);
        }
        addConfigPanel(posX);
    }

    private void addConfigPanel(final double posX)
    {
        final ConfigCategoryPanel panel = new ConfigCategoryPanel();
        panel.setX(posX);
        panel.setY(26.0);
        categoryPanels.add(panel);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        for (final CategoryPanel panel : categoryPanels)
        {
            panel.init();
        }
        MAX_PANEL_HEIGHT = height - 70 - 26.0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        for (final CategoryPanel panel : categoryPanels)
        {
            panel.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        for (final CategoryPanel panel : categoryPanels)
        {
            panel.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (ALLOW_EXIT_ON_ESC)
        {
            super.keyTyped(typedChar, keyCode);
        }
        for (final CategoryPanel panel : categoryPanels)
        {
            panel.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void onGuiClosed()
    {
        if (ClickGUICheat.INSTANCE.saveOnClose.getValue())
        {
            try
            {
                Nebula.INSTANCE.getLogger().info("Writing cheat save state to disk");
                CheatConfig.saveConfig("default");
            } catch (final IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }
}
