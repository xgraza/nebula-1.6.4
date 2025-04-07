package us.nebula.impl.gui.client.component.cheat.value.color;

import us.nebula.api.gui.GUIComponent;
import us.nebula.api.gui.IGUIInputListener;
import us.nebula.api.gui.font.Fonts;
import us.nebula.util.render.RenderUtil;

import java.awt.Color;

/**
 * @author xgraza
 * @since 04/07/25
 */
public final class GradientColorComponent extends GUIComponent implements IGUIInputListener
{
    private final ColorSettingComponent parent;

    public GradientColorComponent(final ColorSettingComponent parent)
    {
        this.parent = parent;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        RenderUtil.roundedRectangle2D(x, y, getWidth(), getHeight(), 1.0f, 0xFFFFFFFF);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {

    }

    @Override
    public double getHeight()
    {
        return getWidth();
    }
}
