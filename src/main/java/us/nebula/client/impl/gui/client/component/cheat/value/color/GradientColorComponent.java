package us.nebula.client.impl.gui.client.component.cheat.value.color;

import us.nebula.client.api.gui.GUIComponent;
import us.nebula.client.api.gui.IGUIInputListener;
import us.nebula.client.util.render.RenderUtil;

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
        final Color color = parent.setting.getValue();

        RenderUtil.gradientRectangle2D(x, y, width, height,
                Color.black.hashCode(),
                Color.black.hashCode(),
                color.hashCode(),
                Color.white.hashCode());
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
