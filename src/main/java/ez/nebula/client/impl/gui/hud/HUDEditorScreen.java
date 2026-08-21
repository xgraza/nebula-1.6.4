package ez.nebula.client.impl.gui.hud;

import ez.nebula.client.impl.module.render.HUDModule;
import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import ez.nebula.client.core.Nebula;
import ez.nebula.client.api.manager.hud.HUDElement;
import ez.nebula.client.impl.gui.hud.component.HUDElementCategoryPanel;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.render.RenderUtil;

import java.awt.Color;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 3/23/26
 */
public final class HUDEditorScreen extends GuiChat
{
    private static final int DEFAULT_BACKGROUND_COLOR = new Color(50, 50, 50, 120).getRGB();
    private static final int HOVERED_BACKGROUND_COLOR = new Color(34, 34, 34, 120).getRGB();
    private static final int DRAGGING_BACKGROUND_COLOR = new Color(241, 223, 109, 120).getRGB();

    private static double SAVED_X = -1, SAVED_Y = -1;
    private static HUDElementCategoryPanel PANEL;
    private HUDElement draggingElement;
    private double dragX = -1.0, dragY = -1.0;
    private boolean scalingElements = true;

    public HUDEditorScreen()
    {
    }

    public HUDEditorScreen(String text)
    {
        super(text);
    }

    @Override
    public void initGui()
    {
        if (PANEL == null)
        {
            PANEL = new HUDElementCategoryPanel();
        }

        if (SAVED_X != -1 && SAVED_Y != -1)
        {
            PANEL.setX(SAVED_X);
            PANEL.setY(SAVED_Y);
        } else
        {
            PANEL.setX(2);
            PANEL.setY(2);
        }
        scalingElements = false;

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (draggingElement == null)
        {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        mc.mcProfiler.startSection("nebulaHUDEditor");

        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            if (!element.isToggled())
            {
                continue;
            }

            if (draggingElement != null && draggingElement.equals(element))
            {
                continue;
            }
            RenderUtil.renderRoundedRectangle(element.getX() - 1,
                    element.getY() - 1,
                    element.getWidth() + 2,
                    element.getHeight() + 2,
                    3.5f,
                    element.isMouseIn(mouseX, mouseY) ? HOVERED_BACKGROUND_COLOR : DEFAULT_BACKGROUND_COLOR);
            element.render(RenderUtil.GAME_RESOLUTION);
        }

        if (draggingElement != null)
        {
            RenderUtil.renderRoundedRectangle(draggingElement.getX() - 1,
                    draggingElement.getY() - 1,
                    draggingElement.getWidth() + 2,
                    draggingElement.getHeight() + 2,
                    3.5f,
                    DRAGGING_BACKGROUND_COLOR);
            draggingElement.render(RenderUtil.GAME_RESOLUTION);

            if (!Mouse.isButtonDown(0))
            {
                draggingElement = null;
                dragX = dragY = -1.0;
                return;
            }
            draggingElement.setX(mouseX - dragX);
            draggingElement.setY(mouseY - dragY);

            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
            {
                handleSnapToGrid();
            }

            if (HUDModule.INSTANCE.forceInBoundsSetting.getValue() && !scalingElements)
            {
                // bounds checks
                if (draggingElement.getX() < 0)
                {
                    draggingElement.setX(0);
                }

                if (draggingElement.getY() < 0)
                {
                    draggingElement.setY(0);
                }

                if (draggingElement.getX() + draggingElement.getWidth() > width)
                {
                    draggingElement.setX(width - draggingElement.getWidth());
                }

                if (draggingElement.getY() + draggingElement.getHeight() > height)
                {
                    draggingElement.setY(height - draggingElement.getHeight());
                }
            }
        } else
        {
            glPushMatrix();
            glScaled(RenderUtil.getGUIScaleFactor(), RenderUtil.getGUIScaleFactor(), RenderUtil.getGUIScaleFactor());

            mouseX /= RenderUtil.getGUIScaleFactor();
            mouseY /= RenderUtil.getGUIScaleFactor();

            PANEL.render(mouseX, mouseY, partialTicks);

            glPopMatrix();
        }

        mc.mcProfiler.endSection();
    }

    private void handleSnapToGrid()
    {
        if (draggingElement == null)
        {
            return;
        }

        // how many pixels within a snap bound
        double leniency = 15;

        double halfWidth = width / 2.0;
        double halfHeight = height / 2.0;

        double elementMidX = draggingElement.getX() + (draggingElement.getWidth() / 2.0);
        double elementMidY = draggingElement.getY() + (draggingElement.getHeight() / 2.0);

        if (Math.abs(halfWidth - elementMidX) <= leniency)
        {
            draggingElement.setX(halfWidth - (draggingElement.getWidth() / 2.0));
        }

        if (Math.abs(halfHeight - elementMidY) <= leniency)
        {
            draggingElement.setY(halfHeight + (draggingElement.getHeight() / 2.0));
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        mouseX /= RenderUtil.getGUIScaleFactor();
        mouseY /= RenderUtil.getGUIScaleFactor();

        PANEL.mouseClicked(mouseX, mouseY, mouseButton);
        if (PANEL.isDragging())
        {
            return;
        }

        if (draggingElement != null)
        {
            return;
        }
        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            if (element.isMouseIn(mouseX, mouseY) && element.isToggled())
            {
                SoundUtil.playClickSound();
                draggingElement = element;
                dragX = mouseX - element.getX();
                dragY = mouseY - element.getY();
                return;
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);
        PANEL.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        SAVED_X = PANEL.getX();
        SAVED_Y = PANEL.getY();
    }
}
