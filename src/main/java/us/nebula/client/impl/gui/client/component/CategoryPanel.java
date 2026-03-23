package us.nebula.client.impl.gui.client.component;

import org.lwjgl.input.Mouse;
import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.api.gui.animation.Animation;
import us.nebula.client.api.gui.animation.AnimationEasing;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.impl.gui.client.ClickGUIScreen;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/06/25
 */
public class CategoryPanel extends GUIComponent implements IGUIInputListener
{
    protected static final double PADDING = 2.0;
    protected static final double PANEL_HEADER_HEIGHT = 16.0;
    protected static final double PANEL_WIDTH = 120.0;

    protected static final int PANEL_HEADER_COLOR = new Color(33, 33, 33).getRGB();
    protected static final int PANEL_BACKGROUND_COLOR = new Color(48, 48, 48).getRGB();

    protected final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 300.0);
    protected final String name;

    private boolean allowScrolling;
    protected int scrollOffset;

    private boolean dragging, allowDragging;
    private double dragX, dragY;

    public CategoryPanel(final String name)
    {
        this.name = name;
        animation.setState(true);
        setHeight(PANEL_HEADER_HEIGHT);
        setWidth(PANEL_WIDTH);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        final double panelHeight = Math.min(getHeight(), ClickGUIScreen.MAX_PANEL_HEIGHT);

        if (isMouseIn(mouseX, mouseY, getX(), getY(), getWidth(), panelHeight)
                && allowScrolling
                && panelHeight >= ClickGUIScreen.MAX_PANEL_HEIGHT)
        {
            final int scroll = Mouse.getDWheel();
            if (scroll > 0)
            {
                final double posY = y + PANEL_HEADER_HEIGHT;
                if (posY + scrollOffset < posY)
                {
                    scrollOffset += 10;
                }
            } else if (scroll < 0)
            {
                if ((y + panelHeight) - (y + scrollOffset + getComponentHeight()) < height)
                {
                    scrollOffset -= 10;
                }
            }
        }

        if (dragging && allowDragging)
        {
            if (!Mouse.isButtonDown(0))
            {
                dragging = false;
            } else
            {
                x = mouseX - dragX;
                y = mouseY - dragY;
            }
        }

        if (!allowScrolling || panelHeight < ClickGUIScreen.MAX_PANEL_HEIGHT)
        {
            scrollOffset = 0;
        }

        RenderUtil.startScissor(x, y - 0.1, width, panelHeight + 0.5);

        RenderUtil.roundedRectangle2D(x, y, width, panelHeight, 6, PANEL_HEADER_COLOR);
        RenderUtil.roundedRectangle2D(x + PADDING, y + height, width - (PADDING * 2), panelHeight - height - PADDING, 2.8f, PANEL_BACKGROUND_COLOR);

        if (animation.getFactor() > 0.0)
        {
            double posY = y + scrollOffset + PANEL_HEADER_HEIGHT;
            for (final GUIComponent component : getChildrenComponentList())
            {
                component.setX(x + PADDING);
                component.setY(posY);
                component.setWidth(width - (PADDING * 2));
                component.setHeight(PANEL_HEADER_HEIGHT - 1.5);

                component.render(mouseX, mouseY, partialTicks);

                posY += component.getHeight() + 1;
            }
        }

        RenderUtil.roundedRectangle2D(x, y, width, PANEL_HEADER_HEIGHT, 6, PANEL_HEADER_COLOR);
        drawHeaderText();

        RenderUtil.endScissor();
    }

    protected void drawHeaderText()
    {
        Fonts.POPPINS.drawStringShadow(name, x + 12 + PADDING, y + 2, -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY))
        {
            if (mouseButton == 0)
            {
                if (!allowDragging)
                {
                    return;
                }
                dragging = true;
                dragX = mouseX - x;
                dragY = mouseY - y;
            } else if (mouseButton == 1)
            {
                SoundUtil.playClickSound();
                animation.setState(!animation.getState());
            }
            return;
        }
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
            {
                ((IGUIInputListener) component).mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        for (final GUIComponent component : getChildrenComponentList())
        {
            if (component instanceof IGUIInputListener)
            {
                ((IGUIInputListener) component).keyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    public double getHeight()
    {
        return super.getHeight() + ((getComponentHeight() + PADDING) * animation.getEasedFactor());
    }

    private double getComponentHeight()
    {
        double height = 0.0;
        for (final GUIComponent component : getChildrenComponentList())
        {
            height += component.getHeight() + 1;
        }
        return height;
    }

    public void setAllowScrolling(boolean allowScrolling)
    {
        this.allowScrolling = allowScrolling;
    }

    public void setAllowDragging(boolean allowDragging)
    {
        this.allowDragging = allowDragging;
    }

    public boolean isDragging()
    {
        return dragging;
    }
}
