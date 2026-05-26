package us.nebula.client.cheat.gui;

import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import us.nebula.client.Nebula;
import us.nebula.client.util.render.gui.GUIComponent;
import us.nebula.client.util.render.gui.font.Fonts;
import us.nebula.client.cheat.Cheat;
import us.nebula.client.cheat.trait.CheatCategory;
import us.nebula.client.cheat.CheatConfig;
import us.nebula.client.cheat.impl.render.ClickGUICheat;
import us.nebula.client.cheat.gui.component.CategoryPanel;
import us.nebula.client.cheat.gui.component.cheat.CheatCategoryPanel;
import us.nebula.client.cheat.gui.component.cheat.CheatPanel;
import us.nebula.client.cheat.gui.component.config.ConfigCategoryPanel;
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
    private final Timer guiResetTimer = new Timer();

    private int oldWidth, oldHeight;

    @Override
    public void initGui()
    {
        if (oldWidth == width && oldHeight == height && !categoryPanels.isEmpty())
        {
            return;
        }

        categoryPanels.clear();

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

        for (final CategoryPanel panel : categoryPanels)
        {
            panel.init();
        }
        MAX_PANEL_HEIGHT = height - 30 - 26.0;

        oldWidth = width;
        oldHeight = height;
    }

    @Override
    public void updateScreen()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_R))
        {
            if (guiResetTimer.hasElapsed(3500L))
            {
                ClickGUICheat.INSTANCE.resetClickGUI();
            }
            return;
        }
        guiResetTimer.resetTime();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (guiResetTimer.getTimeElapsedMS() > 500.0)
        {
            final double time = Math.max(3500.0 - guiResetTimer.getTimeElapsedMS(), 0.0) / 1000.0;
            final String text = String.format("Resetting ClickGUI screen in %.2f second(s).", time);
            Fonts.POPPINS.drawStringShadow(text, width / 2.0 - (Fonts.POPPINS.getStringWidth(text) / 2.0), 10.0, 0xFFFF0000);
        }

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

    private void addConfigPanel(final double posX)
    {
        final ConfigCategoryPanel panel = new ConfigCategoryPanel();
        panel.setX(posX);
        panel.setY(26.0);
        categoryPanels.add(panel);
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

        final String description = cheat.getManifest().description().trim();
        if (description.isEmpty())
        {
            return;
        }

        double boxWidth = 0;
        final List<String> wrappedTextList = Fonts.POPPINS.wrapText(description, 230, true);
        if (wrappedTextList.isEmpty())
        {
            return;
        }
        for (final String line : wrappedTextList)
        {
            final double lineWidth = Fonts.POPPINS.getStringWidth(line);
            if (lineWidth >= boxWidth)
            {
                boxWidth = lineWidth;
            }
        }

        double x = mouseX + 10;
        if (x + boxWidth + 4 > width)
        {
            x = (width - boxWidth) - 4;
        }

        double y = mouseY - 10;
        RenderUtil.roundedRectangle2D(x, y, boxWidth + 8, 2 + (Fonts.POPPINS.getFontHeight() + 1) * wrappedTextList.size(), 5.5f, PANEL_HEADER_COLOR);

        for (final String line : wrappedTextList)
        {
            Fonts.POPPINS.drawStringShadow(line, x + 4, y + 1, -1);
            y += Fonts.POPPINS.getFontHeight();
        }
    }
}
