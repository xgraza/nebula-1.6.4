package ez.nebula.client.impl.gui.module.component;

import ez.nebula.client.api.render.font.AWTFontRenderer;
import org.lwjgl.input.Mouse;
import ez.nebula.client.api.render.trait.GUIComponent;
import ez.nebula.client.api.render.trait.IGUIInputListener;
import ez.nebula.client.api.render.animation.Animation;
import ez.nebula.client.api.render.animation.AnimationEasing;
import ez.nebula.client.api.render.font.Fonts;
import ez.nebula.client.impl.gui.module.ClickGUIScreen;
import ez.nebula.client.util.io.SoundUtil;
import ez.nebula.client.util.render.RenderUtil;

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

    protected static final int SCROLL_ADVANCE = 10;

    protected static final int PANEL_HEADER_COLOR = new Color(33, 33, 33).getRGB();
    protected static final int PANEL_BACKGROUND_COLOR = new Color(48, 48, 48).getRGB();

    protected final Animation animation = new Animation(
            AnimationEasing.EXPO_IN_OUT, 150.0);
    protected final String name;
    private final Character icon;

    private boolean allowScrolling;
    protected double scrollOffset, targetScrollOffset, scrollSpeed = 0.15;

    private boolean dragging, allowDragging;
    private double dragX, dragY;

    public CategoryPanel(final String name, final Character icon)
    {
        this.name = name;
        this.icon = icon;
        animation.setState(true);
        setHeight(PANEL_HEADER_HEIGHT);
        setWidth(PANEL_WIDTH);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        AWTFontRenderer.DYNAMIC_FONT_RESIZING = false;
        final double scaledMaxHeight = ClickGUIScreen.MAX_PANEL_HEIGHT / RenderUtil.getGUIScaleFactor();
        final double panelHeight = Math.min(getHeight(), scaledMaxHeight);

        if (isMouseIn(mouseX, mouseY, getX(), getY(), getWidth(), panelHeight)
                && allowScrolling
                && panelHeight >= scaledMaxHeight)
        {
            final int scroll = Mouse.getDWheel();
            if (scroll > 0)
            {
                final double posY = y + PANEL_HEADER_HEIGHT;
                if (posY + targetScrollOffset < posY)
                {
                    targetScrollOffset += SCROLL_ADVANCE;
                }
            } else if (scroll < 0)
            {
                if ((y + panelHeight) - (y + targetScrollOffset + getComponentHeight()) <= height)
                {
                    targetScrollOffset -= SCROLL_ADVANCE;
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

        if (!allowScrolling || panelHeight < scaledMaxHeight)
        {
            targetScrollOffset = 0;
        }

        scrollOffset += (targetScrollOffset - scrollOffset) * scrollSpeed;
        if (Math.abs(targetScrollOffset - scrollOffset) <= 0.01)
        {
            scrollOffset = targetScrollOffset;
        }

        RenderUtil.startScissor(x, y, width, panelHeight);

        RenderUtil.renderRoundedRectangle(x, y, width, panelHeight, 6, PANEL_HEADER_COLOR);
        RenderUtil.renderRoundedRectangle(x + PADDING, y + height, width - (PADDING * 2), panelHeight - height - PADDING, 2.8f, PANEL_BACKGROUND_COLOR);

        if (animation.getFactor() > 0.0)
        {
            double posY = y + scrollOffset + PANEL_HEADER_HEIGHT;

            // fix bug where when scrolled theres a gap between the end of the panel vs the last element
            final double panelGap = (y + panelHeight) - (posY + getComponentHeight());
            if (panelGap > PADDING)
            {
                posY += (panelGap - PADDING);
            }

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

        RenderUtil.renderRoundedRectangle(x, y, width, PANEL_HEADER_HEIGHT, 6, PANEL_HEADER_COLOR);
        drawHeaderText();

        // render hack:
        // since we scissor down to the max height, elements within the scissor box can overflow the
        // panel bounds (bounds as in the actual panel rounded rectangles, since theres a boarder around the entire thing)
        // so we render a small rectangle over the very bottom so it looks a little cleaner
        // such a small little thing that doesn't matter but once I noticed it, it began to piss me off...
        if (panelHeight >= scaledMaxHeight)
        {
            RenderUtil.renderRoundedRectangle(x, y + panelHeight - PADDING, width, PADDING, 4.9f, PANEL_HEADER_COLOR);
        }

        RenderUtil.endScissor();
        AWTFontRenderer.DYNAMIC_FONT_RESIZING = true;
    }

    protected void drawHeaderText()
    {
        double iconWidth = 0.0;
        if (icon != null)
        {
            Fonts.TYPEFACE.drawStringShadow(String.valueOf(icon), x + PADDING, y + 5, 0xAAAAAA);
            iconWidth = Fonts.TYPEFACE.getStringWidth(String.valueOf(icon)) + (PADDING);
        }
        Fonts.POPPINS.drawStringShadow(name, x + iconWidth + PADDING, y + 2, -1);
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

    public void setScrollSpeed(double scrollSpeed)
    {
        this.scrollSpeed = scrollSpeed;
    }

    public boolean isDragging()
    {
        return dragging;
    }
}
