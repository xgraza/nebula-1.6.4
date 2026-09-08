package ez.nebula.client.impl.gui.clickgui;

import ez.nebula.client.Nebula;
import ez.nebula.client.api.manager.module.trait.ModuleCategory;
import ez.nebula.client.impl.config.ModuleConfig;
import ez.nebula.client.impl.gui.clickgui.component.CategoryPanel;
import ez.nebula.client.impl.gui.clickgui.component.IComponentDescription;
import ez.nebula.client.impl.gui.clickgui.component.config.ConfigCategoryPanel;
import ez.nebula.client.impl.gui.clickgui.component.hud.HUDElementCategoryPanel;
import ez.nebula.client.impl.gui.clickgui.component.module.ModuleCategoryPanel;
import ez.nebula.client.impl.module.render.ClickGUIModule;
import ez.nebula.client.util.math.Timer;
import ez.nebula.client.util.render.font.AWTFontRenderer;
import ez.nebula.client.util.render.font.Fonts;
import ez.nebula.client.util.render.gui.Render2D;
import ez.nebula.client.util.render.gui.trait.GUIComponent;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class ClickGUIScreen extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger("ClickGUI");
    
    private static final int PANEL_HEADER_COLOR = new Color(33, 33, 33).getRGB();
    private static final double DEFAULT_PANEL_Y = 10.0;

    public static boolean ALLOW_EXIT_ON_ESC = true;
    public static double MAX_PANEL_HEIGHT;

    private final LinkedList<CategoryPanel> categoryPanels = new LinkedList<>();
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

        guiResetTimer.resetTime();
        categoryPanels.clear();

        double posX = 8.0;
        for (final ModuleCategory category : ModuleCategory.values())
        {
            final ModuleCategoryPanel panel = new ModuleCategoryPanel(category);
            panel.setX(posX);
            panel.setY(DEFAULT_PANEL_Y);
            posX += panel.getWidth() + 3;
            categoryPanels.add(panel);
        }
        final CategoryPanel hudElementPanel = new HUDElementCategoryPanel();
        hudElementPanel.setX(posX);
        hudElementPanel.setY(DEFAULT_PANEL_Y);
        categoryPanels.add(hudElementPanel);

        for (final CategoryPanel panel : categoryPanels)
        {
            panel.init();
        }

        MAX_PANEL_HEIGHT = height - 20 - DEFAULT_PANEL_Y;

        oldWidth = width;
        oldHeight = height;

        if (!Nebula.OPENED_GUI_BEFORE)
        {
            Nebula.OPENED_GUI_BEFORE = true;
            Nebula.TOASTS.info("ClickGUI",
                    "To bind modules, use the middle mouse button to bind, and then double click the mouse button to unbind a module",
                    15_000L);
        }
    }

    @Override
    public void updateScreen()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_R))
        {
            if (guiResetTimer.hasElapsed(3500L))
            {
                ClickGUIModule.INSTANCE.resetClickGUI();
            }
            return;
        }
        guiResetTimer.resetTime();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (mc.thePlayer == null || mc.theWorld == null)
        {
            drawDefaultBackground();
        }

        glPushMatrix();
        glScaled(Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor(), Render2D.getGUIScaleFactor());

        mouseX /= Render2D.getGUIScaleFactor();
        mouseY /= Render2D.getGUIScaleFactor();

        if (guiResetTimer.getTimeElapsedMS() > 500.0)
        {
            final double time = Math.max(3500.0 - guiResetTimer.getTimeElapsedMS(), 0.0) / 1000.0;
            final String text = String.format("Resetting ClickGUI screen in %.2f second(s).", time);
            Fonts.POPPINS.drawStringShadow(text, width / 2.0 - (Fonts.POPPINS.getStringWidth(text) / 2.0), 0, 0xFFFF0000);
        }

        for (final CategoryPanel panel : categoryPanels)
        {
            panel.render(mouseX, mouseY, partialTicks);
        }
        findAndDrawHoveredModuleDescription(mouseX, mouseY);

        glPopMatrix();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        mouseX /= Render2D.getGUIScaleFactor();
        mouseY /= Render2D.getGUIScaleFactor();
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
        if (ClickGUIModule.INSTANCE.saveOnCloseSetting.getValue())
        {
            try
            {
                LOGGER.info("Writing cheat save state to disk");
                ModuleConfig.saveConfig("default");
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

    private ConfigCategoryPanel createConfigPanel(final double posX)
    {
        final ConfigCategoryPanel panel = new ConfigCategoryPanel();
        panel.setX(posX);
        panel.setY(DEFAULT_PANEL_Y);
        panel.init();
        return panel;
    }

    private void findAndDrawHoveredModuleDescription(final int mouseX, final int mouseY)
    {
        if (!ClickGUIModule.INSTANCE.hoverDescriptionSetting.getValue() || Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
        {
            return;
        }

        final List<GUIComponent> components = new ArrayList<>();
        for (final CategoryPanel panel : categoryPanels)
        {
            if (!panel.isOpen())
            {
                continue;
            }
            for (final GUIComponent component : panel.getChildrenComponentList())
            {
                if (!(component instanceof IComponentDescription))
                {
                    continue;
                }
                if (component.isMouseIn(mouseX, mouseY))
                {
                    drawHoveredDescription(((IComponentDescription) component).getDescription(), mouseX, mouseY);
                    return;
                } else if (component.isOpen())
                {
                    for (final GUIComponent child : component.getChildrenComponentList())
                    {
                        if (child instanceof IComponentDescription)
                        {
                            components.add(child);
                        }
                    }
                }
            }
        }

        for (final GUIComponent component : components)
        {
            if (component.isMouseIn(mouseX, mouseY))
            {
                drawHoveredDescription(((IComponentDescription) component).getDescription(), mouseX, mouseY);
                return;
            } else if (component.isOpen())
            {
                for (final GUIComponent child : component.getChildrenComponentList())
                {
                    if (child instanceof IComponentDescription)
                    {
                        components.add(child);
                    }
                }
            }
        }
        descriptionHoverTimer.resetTime();
    }

    private void drawHoveredDescription(final String description, final int mouseX, final int mouseY)
    {
        if (!descriptionHoverTimer.hasElapsed(600L) || description.isEmpty())
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

        double screenWidth = width / Render2D.getGUIScaleFactor();

        double x = mouseX + 10;
        if (x + boxWidth + 4 > screenWidth)
        {
            x = (screenWidth - boxWidth) - 4;
        }

        double y = mouseY - 10;
        Render2D.roundedRectangle(x, y, boxWidth + 8, 2 + (Fonts.POPPINS.getFontHeight() + 1) * wrappedTextList.size(), 5.5f, PANEL_HEADER_COLOR);

        AWTFontRenderer.DYNAMIC_FONT_RESIZING = false;
        for (final String line : wrappedTextList)
        {
            Fonts.POPPINS.drawStringShadow(line, x + 4, y + 1, -1);
            y += Fonts.POPPINS.getFontHeight();
        }
        AWTFontRenderer.DYNAMIC_FONT_RESIZING = true;
    }
}
