package us.nebula.client.impl.gui.client.component.config;

import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.api.gui.font.Fonts;
import us.nebula.client.util.io.SoundUtil;
import us.nebula.client.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 03/06/25
 */
public class ConfigTextButton extends GUIComponent implements IGUIInputListener
{
    private static final int BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();
    private static final double PADDING = 1.0;

    private final String text;

    public ConfigTextButton(final String text)
    {
        this.text = text;

        setWidth(Fonts.POPPINS_SMALL.getStringWidth(text) + (PADDING * 4));
        setHeight(Fonts.POPPINS_SMALL.getFontHeight() + (PADDING * 2));
    }

    public void onButtonPress()
    {

    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.roundedRectangle2D(x, y, getWidth(), getHeight(), 3.5f, BACKGROUND_COLOR);
        Fonts.POPPINS_SMALL.drawStringShadow(text, x + (PADDING * 2), y + Fonts.getMiddlePoint(getHeight(), Fonts.POPPINS_SMALL.getFontHeight()), -1);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (isMouseIn(mouseX, mouseY) && mouseButton == 0)
        {
            SoundUtil.playClickSound();
            onButtonPress();
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }
}
