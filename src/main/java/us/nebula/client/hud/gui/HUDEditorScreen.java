package us.nebula.client.hud.gui;

import net.minecraft.client.gui.GuiChat;
import org.lwjgl.input.Mouse;
import us.nebula.client.Nebula;
import us.nebula.client.cheat.impl.render.HUDCheat;
import us.nebula.client.hud.HUDElement;
import us.nebula.client.hud.gui.component.HUDElementCategoryPanel;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;

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

    private HUDElementCategoryPanel panel;
    private HUDElement draggingElement;
    private double dragX = -1.0, dragY = -1.0;

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
        panel = new HUDElementCategoryPanel();

        if (SAVED_X != -1 && SAVED_Y != -1)
        {
            panel.setX(SAVED_X);
            panel.setY(SAVED_Y);
        } else
        {
            panel.setX(2);
            panel.setY(2);
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (draggingElement == null)
        {
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            if (HUDCheat.INSTANCE.forceInBoundsSetting.getValue())
            {
                // bounds checks
                if (element.getX() < 0)
                {
                    element.setX(0);
                }

                if (element.getY() < 0)
                {
                    element.setY(0);
                }

                if (element.getX() + element.getWidth() > width)
                {
                    element.setX(width - element.getWidth());
                }

                if (element.getY() + element.getHeight() > height)
                {
                    element.setY(height - element.getHeight());
                }
            }

            if (!element.isToggled())
            {
                continue;
            }

            if (draggingElement != null && draggingElement.equals(element))
            {
                continue;
            }
            RenderUtil.roundedRectangle2D(element.getX() - 1,
                    element.getY() - 1,
                    element.getWidth() + 2,
                    element.getHeight() + 2,
                    3.5f,
                    element.isMouseIn(mouseX, mouseY) ? HOVERED_BACKGROUND_COLOR : DEFAULT_BACKGROUND_COLOR);
            element.render(RenderUtil.GAME_RESOLUTION);
        }

        if (draggingElement != null)
        {
            RenderUtil.roundedRectangle2D(draggingElement.getX() - 1,
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
        } else
        {
            panel.render(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        panel.mouseClicked(mouseX, mouseY, mouseButton);
        if (panel.isDragging())
        {
            return;
        }

        if (draggingElement != null)
        {
            return;
        }
        for (final HUDElement element : Nebula.INSTANCE.getHUDManager().getAll())
        {
            if (element.isMouseIn(mouseX, mouseY))
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
        panel.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed()
    {
        super.onGuiClosed();

        SAVED_X = panel.getX();
        SAVED_Y = panel.getY();
    }
}
