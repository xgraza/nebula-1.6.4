package us.nebula.client.impl.gui.client;

import net.minecraft.client.gui.GuiScreen;
import us.nebula.client.Nebula;
import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.api.manager.cheat.Cheat;
import us.nebula.client.api.manager.cheat.CheatCategory;
import us.nebula.client.api.manager.cheat.CheatConfig;
import us.nebula.client.impl.cheat.render.ClickGUICheat;
import us.nebula.client.impl.gui.client.component.CategoryPanel;
import us.nebula.client.impl.gui.client.component.cheat.CheatCategoryPanel;
import us.nebula.client.impl.gui.client.component.cheat.CheatPanel;
import us.nebula.client.impl.gui.client.component.config.ConfigCategoryPanel;
import us.nebula.client.util.math.Timer;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ClickGUIScreen extends GuiScreen
{
    private static final int PANEL_HEADER_COLOR = new Color(33, 33, 33).getRGB();

    public static boolean ALLOW_EXIT_ON_ESC = true;
    public static double MAX_PANEL_HEIGHT;

    private final List<CategoryPanel> categoryPanels = new LinkedList<>();
    private final Timer descriptionHoverTimer = new Timer();

    public ClickGUIScreen()
    {
        double posX = 8.0;
        for (final CheatCategory category : CheatCategory.values())
        {
            final CheatCategoryPanel panel = new CheatCategoryPanel(category);
            panel.setX(posX);
            panel.setY(26.0);
            posX += panel.getWidth() + 3;
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
        findAndDrawHoveredCheatDescription(mouseX, mouseY);
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
        if (ClickGUICheat.INSTANCE.saveOnCloseSetting.getValue())
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

    private void findAndDrawHoveredCheatDescription(final int mouseX, final int mouseY)
    {
        if (!ClickGUICheat.INSTANCE.hoverDescriptionSetting.getValue())
        {
            return;
        }
        for (final CategoryPanel categoryPanel : categoryPanels)
        {
            final List<GUIComponent> childrenComponents = categoryPanel.getChildrenComponentList();
            if (childrenComponents.isEmpty())
            {
                continue;
            }
            for (final GUIComponent c : childrenComponents)
            {
                if (c instanceof CheatPanel)
                {
                    final CheatPanel cheatPanel = (CheatPanel) c;
                    if (cheatPanel.isMouseIn(mouseX, mouseY))
                    {
                        drawCheatDescription(cheatPanel.getCheat(), mouseX, mouseY);
                        return;
                    }
                }
            }
        }
        descriptionHoverTimer.resetTime();
    }

    private void drawCheatDescription(final Cheat cheat, final int mouseX, final int mouseY)
    {
        if (!descriptionHoverTimer.hasElapsed(600L))
        {
            return;
        }

        final String description = cheat.getManifest().description();

        final double width = Fonts.POPPINS.getStringWidth(description) + 8;
        final double height = Fonts.POPPINS.getFontHeight() + 2;

        double x = mouseX + 10;
        if (x + width + 4 > this.width)
        {
            x = (this.width - width) - 4;
        }

        double y = mouseY - 10;

        RenderUtil.roundedRectangle2D(x, y, width, height, 5.5f, PANEL_HEADER_COLOR);
        Fonts.POPPINS.drawStringShadow(description, x + 4, y + 1, -1);
    }
}
