package us.nebula.impl.gui.client.component;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.animation.Animation;
import us.nebula.api.gui.animation.AnimationEasing;
import us.nebula.api.gui.font.Fonts;
import us.nebula.util.io.SoundUtil;
import us.nebula.util.render.RenderUtil;

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
        RenderUtil.startScissor(x, y - 1, width, getHeight() + PADDING);

        RenderUtil.roundedRectangle2D(x, y, width, getHeight(), 6, PANEL_HEADER_COLOR);
        RenderUtil.roundedRectangle2D(x + PADDING, y + height, width - (PADDING * 2), getHeight() - height - PADDING, 2.8f, PANEL_BACKGROUND_COLOR);

        drawHeaderText();

        if (animation.getFactor() > 0.0)
        {
            double posY = y + PANEL_HEADER_HEIGHT;
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
        double h = -1;
        for (final GUIComponent component : getChildrenComponentList())
        {
            h += component.getHeight() + 1;
        }
        return (super.getHeight())
                + ((h + PADDING) * animation.getEasedFactor());
    }
}
